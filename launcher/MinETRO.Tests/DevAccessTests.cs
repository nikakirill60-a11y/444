using System.Net;
using System.Text;
using System.Text.Json;
using MinETRO.Core;

internal static class DevAccessTests
{
    private static readonly AccountsConfig Config = new("https://test-project.supabase.co","sb_publishable_test_public_only");
    private const string Id = "11111111-1111-4111-8111-111111111111";
    private const string Session = "{\"access_token\":\"dev-test-token\",\"refresh_token\":\"dev-test-refresh\",\"expires_in\":3600,\"user\":{\"id\":\""+Id+"\"}}";
    private static HttpResponseMessage Json(string json,HttpStatusCode status=HttpStatusCode.OK)=>new(status){Content=new StringContent(json,Encoding.UTF8,"application/json")};
    private static void Assert(bool value){if(!value)throw new Exception("Dev access assertion failed");}
    private static async Task Reject(Func<Task> run){try{await run();}catch(IOException){return;}throw new Exception("Expected dev rejection");}
    private static PackManifest Manifest => PackManifest.ReleaseDefault with { Version="dev-baseline-test",Name="MinETRO dev",Archive=new("https://untrusted.test/pack.zip",new string('a',64),1),RequiredFiles=[new("mods/test.jar",new string('b',64))] };
    private static string Release(string key="baseline/MinETRO.zip")=>JsonSerializer.Serialize(new {manifest=Manifest,objectKey=key},PackManifest.Json);
    public static async Task Run()
    {
        int passed=0;async Task Test(string name,Func<Task> action){await action();passed++;Console.WriteLine("PASS dev: "+name);}
        await Test("public, unapproved and missing archive channels never install",()=>{
            foreach(var state in Enum.GetValues<DevAccessState>())Assert(!Channels.CanInstall(PackChannel.Public,state,Manifest));
            foreach(var state in Enum.GetValues<DevAccessState>().Where(s=>s!=DevAccessState.Approved))Assert(!Channels.CanInstall(PackChannel.Dev,state,Manifest));
            Assert(!Channels.CanInstall(PackChannel.Dev,DevAccessState.Approved,PackManifest.ReleaseDefault));
            Assert(Channels.CanInstall(PackChannel.Dev,DevAccessState.Approved,Manifest));return Task.CompletedTask;
        });
        await Test("signed out and recovery sessions cannot request or download dev",async()=>{
            using var stub=new Stub((_,_)=>Json(Session));using var api=new Accounts(Config,stub);
            await Reject(()=>api.DevStatus(default));await Reject(()=>api.RequestDev(default));await Reject(()=>api.DevRelease(default));Assert(stub.Calls.Count==0);
            await api.VerifyCode("test@example.test","12345678",true,default);
            await Reject(()=>api.DevRelease(default));await Reject(()=>api.RequestDev(default));Assert(stub.Calls.Count==1);
        });
        await Test("all dev states are scoped to signed-in account and unknown fails closed",async()=>{
            foreach(var status in new[]{"none","pending","approved","rejected","unexpected"}){
                using var stub=new Stub((_,index)=>Json(index==0?Session:status=="none"?"[]":"[{\"status\":\""+status+"\"}]"));using var api=new Accounts(Config,stub);
                await api.SignIn("test@example.test","test-password",default);
                if(status=="unexpected")await Reject(()=>api.DevStatus(default));else Assert((await api.DevStatus(default)).ToString().Equals(status,StringComparison.OrdinalIgnoreCase));
                Assert(stub.Calls[1].PathAndQuery=="/rest/v1/dev_access_requests?user_id=eq."+Id+"&select=status");Assert(stub.Bearers[1]=="dev-test-token");
            }
        });
        await Test("request uses server RPC, never client-side approval",async()=>{
            using var stub=new Stub((_,index)=>Json(index==0?Session:"\"pending\""));using var api=new Accounts(Config,stub);await api.SignIn("test@example.test","test-password",default);
            Assert(await api.RequestDev(default)==DevAccessState.Pending);Assert(stub.Calls[1].AbsolutePath=="/rest/v1/rpc/request_dev_access");
        });
        await Test("server denial and unpublished release do not unlock downloads",async()=>{
            foreach(var status in new[]{HttpStatusCode.OK,HttpStatusCode.Forbidden}){
                using var stub=new Stub((_,index)=>index==0?Json(Session):Json("null",status));using var api=new Accounts(Config,stub);await api.SignIn("test@example.test","test-password",default);
                if(status==HttpStatusCode.OK)Assert(await api.DevRelease(default)==null);else await Reject(()=>api.DevRelease(default));
            }
        });
        await Test("release URLs are replaced by project-bound private storage URL",async()=>{
            using var stub=new Stub((_,index)=>Json(index==0?Session:Release()));using var api=new Accounts(Config,stub);await api.SignIn("test@example.test","test-password",default);
            var m=await api.DevRelease(default);Assert(m?.Archive?.Url==Config.Url+"/storage/v1/object/authenticated/dev-packs/baseline/MinETRO.zip");Assert(m.Name=="MinETRO dev");
        });
        await Test("traversal and external download destinations rejected without token leak",async()=>{
            using var stub=new Stub((_,index)=>Json(index==0?Session:Release()));using var api=new Accounts(Config,stub);await api.SignIn("test@example.test","test-password",default);
            await Reject(()=>api.OpenDevDownload(new Uri("https://external.test/steal"),default));Assert(stub.Calls.All(u=>u.Host=="test-project.supabase.co"));
            using var badStub=new Stub((_,index)=>Json(index==0?Session:Release("../pack.zip")));using var badApi=new Accounts(Config,badStub);await badApi.SignIn("test@example.test","test-password",default);await Reject(()=>badApi.DevRelease(default));
        });
        await Test("private download rechecks release and authorization and never follows redirects",async()=>{
            foreach(var redirect in new[]{false,true}){
                using var stub=new Stub((request,index)=>index==0?Json(Session):request.RequestUri!.AbsolutePath.StartsWith("/rest/")?Json(Release()):Json("x",redirect?HttpStatusCode.Redirect:HttpStatusCode.OK));
                using var api=new Accounts(Config,stub);await api.SignIn("test@example.test","test-password",default);
                var uri=new Uri(Config.Url+"/storage/v1/object/authenticated/dev-packs/baseline/MinETRO.zip");
                if(redirect)await Reject(()=>api.OpenDevDownload(uri,default));else {using var result=await api.OpenDevDownload(uri,default);Assert(result.IsSuccessStatusCode);}
                Assert(stub.Calls.Count==3);Assert(stub.Calls[1].AbsolutePath=="/rest/v1/rpc/get_dev_release");Assert(stub.Calls[2]==uri);Assert(stub.Bearers[2]=="dev-test-token");
            }
        });
        Assert(passed==8);Console.WriteLine("PASS: 8 dev access protocol scenarios; real archive/game not tested");
    }
    private sealed class Stub(Func<HttpRequestMessage,int,HttpResponseMessage> reply):HttpMessageHandler
    {
        public List<Uri> Calls {get;}=[];public List<string?> Bearers {get;}=[];
        protected override Task<HttpResponseMessage> SendAsync(HttpRequestMessage request,CancellationToken ct){ct.ThrowIfCancellationRequested();int index=Calls.Count;Calls.Add(request.RequestUri!);Bearers.Add(request.Headers.Authorization?.Parameter);return Task.FromResult(reply(request,index));}
    }
}
