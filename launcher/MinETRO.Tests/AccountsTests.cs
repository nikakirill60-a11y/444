using System.Net;
using System.Text;
using System.Text.Json;
using MinETRO.Core;

internal static class AccountsTests
{
    private static readonly Guid Id = Guid.Parse("00000000-0000-4000-8000-000000000001");
    private static readonly AccountsConfig Config = new("https://test-project.supabase.co", "sb_publishable_test_public_only");
    private static string Session(int expires = 3600) => JsonSerializer.Serialize(new { access_token="access-not-a-real-token", refresh_token="refresh-not-a-real-token", expires_in=expires, user=new {id=Id} });
    private static void Assert(bool ok) { if(!ok)throw new Exception("Account assertion failed"); }
    private static async Task Reject(Func<Task> action) { try { await action(); } catch(Exception e) when(e is IOException or InvalidDataException) {return;} throw new Exception("Expected account rejection"); }
    public static async Task Run()
    {
        int count=0;
        async Task Test(string name, Func<Task> action) { await action();count++;Console.WriteLine("PASS accounts: "+name); }
        await Test("disabled service makes no network calls",async()=> {
            using var handler=new Stub();using var api=new Accounts(new(),handler);
            await Reject(()=>api.SignIn("player@example.test","secret",default));Assert(handler.Calls.Count==0);
        });
        await Test("public config rejects secrets, non-HTTPS and URL injection",async()=> {
            foreach(var config in new[]{Config with {Url="http://test-project.supabase.co"},Config with{Url="https://evil.test"},Config with{Url="https://test-project.supabase.co/?x=1"},Config with{Url="https://test-project.supabase.co/auth"},Config with{PublicKey="sb_secret_not_allowed"},Config with{PublicKey="eyJhbGciOiJub25lIn0.eyJyb2xlIjoic2VydmljZV9yb2xlIn0.signature"}})
                await Reject(()=>{config.Validate();return Task.CompletedTask;});
            Config.Validate();new AccountsConfig().Validate();
        });
        await Test("nickname, email, password validated before signup",async()=> {
            using var handler=new Stub();using var api=new Accounts(Config,handler);
            foreach(var nick in new[]{"aa","bad name","../evil","Кирилл",new string('x',17)})await Reject(()=>api.SignUp("p@example.test",new string('x',12),nick,default));
            await Reject(()=>api.SignUp("not-an-email",new string('x',12),"Player",default));
            await Reject(()=>api.SignUp("p@example.test","short","Player",default));Assert(handler.Calls.Count==0);
        });
        await Test("signup has metadata and cannot silently log in",async()=> {
            using var handler=new Stub(Session());using var api=new Accounts(Config,handler);
            await api.SignUp(" p@example.test ",new string('x',12),"Player_1",default);
            var call=handler.Calls.Single();Assert(call.Path=="/auth/v1/signup" && call.Bearer==null && call.Key==Config.PublicKey);
            using var body=JsonDocument.Parse(call.Body);Assert(body.RootElement.GetProperty("data").GetProperty("nickname").GetString()=="Player_1");Assert(!api.SignedIn);
        });
        await Test("login and own profile use scoped JWT",async()=> {
            using var handler=new Stub(Session(),JsonSerializer.Serialize(new[]{new{id=Id,nickname="Player",skin_path=(string?)null,skin_model="alex"}}));using var api=new Accounts(Config,handler);
            await api.SignIn("p@example.test","password",default);var profile=await api.Profile(default);
            Assert(api.UserId==Id && profile.Nickname=="Player" && profile.SkinModel=="alex");
            Assert(handler.Calls[1].Path.StartsWith("/rest/v1/profiles?id=eq."+Id) && handler.Calls[1].Bearer=="access-not-a-real-token");
        });
        await Test("expired token refreshes before authenticated request",async()=> {
            using var handler=new Stub(Session(1),Session(),"[]");using var api=new Accounts(Config,handler);
            await api.SignIn("p@example.test","password",default);await Reject(()=>api.Profile(default));
            Assert(handler.Calls[1].Path=="/auth/v1/token?grant_type=refresh_token" && handler.Calls[1].Bearer==null);
            Assert(handler.Calls[1].Body.Contains("refresh-not-a-real-token"));
        });
        await Test("signup code, recovery and password reset are separate",async()=> {
            using var handler=new Stub("{}",Session(),"{}","{}");using var api=new Accounts(Config,handler);
            await Reject(()=>api.SetRecoveredPassword(new string('x',12),default));
            await api.SendRecovery("p@example.test",default);await api.VerifyCode("p@example.test","123456",true,default);
            Assert(api.RecoverySession);await api.SetRecoveredPassword(new string('y',12),default);Assert(!api.SignedIn);
            Assert(handler.Calls[1].Body.Contains("recovery") && handler.Calls[2].Method=="PUT" && handler.Calls[3].Path.EndsWith("scope=local"));
        });
        await Test("errors sanitized, redirects not followed",async()=> {
            using var handler=new Stub();handler.Status=HttpStatusCode.Redirect;using var api=new Accounts(Config,handler);
            await Reject(()=>api.SignIn("p@example.test","password",default));Assert(handler.Calls.Count==1 && !api.SignedIn);
            handler.Status=HttpStatusCode.TooManyRequests;
            try{await api.SignIn("p@example.test","password",default);throw new Exception("Expected error");}catch(AccountException e){Assert(!e.Message.Contains("private-details") && e.Message.Contains("попыток"));}
        });
        await Test("logout clears local identity even when remote logout fails",async()=> {
            using var handler=new Stub(Session());using var api=new Accounts(Config,handler);await api.SignIn("p@example.test","password",default);
            handler.Status=HttpStatusCode.ServiceUnavailable;await Reject(()=>api.SignOut(default));Assert(!api.SignedIn);
        });
        await Test("skin format bounds and slim legacy rejection",async()=> {
            using var handler=new Stub(Session());using var api=new Accounts(Config,handler);await api.SignIn("p@example.test","password",default);
            foreach(var png in new[]{new byte[0],new byte[262145],Png(128,64),Png(64,128)})await Reject(()=>api.UploadSkin(png,"steve",default));
            await Reject(()=>api.UploadSkin(Png(64,32),"alex",default));await Reject(()=>api.UploadSkin(Png(64,64),"unknown",default));Assert(handler.Calls.Count==1);
        });
        await Test("skin upload is bounded to own fixed object and model profile",async()=> {
            using var handler=new Stub(Session(),"{}","{}");using var api=new Accounts(Config,handler);await api.SignIn("p@example.test","password",default);
            await api.UploadSkin(Png(64,64),"alex",default);
            Assert(handler.Calls[1].Path=="/storage/v1/object/skins/"+Id+"/skin.png" && handler.Calls[1].Upsert=="true" && handler.Calls[1].ContentType=="image/png");
            Assert(handler.Calls[2].Method=="PATCH" && handler.Calls[2].Body.Contains("alex"));
            await Reject(()=>api.DownloadSkin(new(Id,"Player","https://evil.test/steal","steve"),default));Assert(handler.Calls.Count==3);
        });
        await Test("unauthorized authenticated request invalidates session",async()=> {
            using var handler=new Stub(Session());using var api=new Accounts(Config,handler);await api.SignIn("p@example.test","password",default);
            handler.Status=HttpStatusCode.Unauthorized;await Reject(()=>api.Rename("Player_2",default));Assert(!api.SignedIn);
        });
        Assert(count==12);Console.WriteLine($"PASS: {count} account protocol scenarios (mock HTTP; live Supabase NOT run)");
    }
    private static byte[] Png(int width,int height)
    {
        var data=new byte[33];new byte[]{137,80,78,71,13,10,26,10}.CopyTo(data,0);"IHDR"u8.CopyTo(data.AsSpan(12));
        System.Buffers.Binary.BinaryPrimitives.WriteInt32BigEndian(data.AsSpan(16,4),width);System.Buffers.Binary.BinaryPrimitives.WriteInt32BigEndian(data.AsSpan(20,4),height);return data;
    }
    private sealed record Call(string Method,string Path,string? Key,string? Bearer,string Body,string? ContentType,string? Upsert);
    private sealed class Stub(params string[] responses):HttpMessageHandler
    {
        public List<Call> Calls {get;}=[];
        public HttpStatusCode Status {get;set;}=HttpStatusCode.OK;
        protected override async Task<HttpResponseMessage> SendAsync(HttpRequestMessage request,CancellationToken ct)
        {
            ct.ThrowIfCancellationRequested();var index=Calls.Count;
            Calls.Add(new(request.Method.Method,request.RequestUri!.PathAndQuery,request.Headers.TryGetValues("apikey",out var keys)?keys.Single():null,request.Headers.Authorization?.Parameter,request.Content==null?"":await request.Content.ReadAsStringAsync(ct),request.Content?.Headers.ContentType?.MediaType,request.Headers.TryGetValues("x-upsert",out var upsert)?upsert.Single():null));
            return new(Status){Content=new StringContent(index<responses.Length?responses[index]:"private-details-and-tokens",Encoding.UTF8,"application/json")};
        }
    }
}
