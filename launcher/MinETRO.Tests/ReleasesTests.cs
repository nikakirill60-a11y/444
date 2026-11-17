using System.Net;
using System.Net.Http.Json;
using System.Text;
using System.Text.Json;
using MinETRO.Core;

internal static class ReleasesTests
{
    public static async Task Run()
    {
        int passed=0;async Task Test(string name,Func<Task> action){await action();passed++;Console.WriteLine("PASS releases: "+name);}
        void Assert(bool value){if(!value)throw new Exception("Release assertion failed");}
        async Task Reject(Func<Task> action){try{await action();}catch(Exception e)when(e is IOException or InvalidDataException){return;}throw new Exception("Expected release rejection");}
        string prefix="https://github.com/"+GitHubReleases.Repository+"/releases/download/pack-v1/";
        var manifest=PackManifest.ReleaseDefault with{Version="pack-v1",Archive=new(prefix+GitHubReleases.ArchiveAsset,new string('a',64),123),RequiredFiles=[new("mods/metrocraft-port.jar",new string('b',64))]};
        object Asset(string name,string? url=null,long size=123,string? digest=null)=>new{name,browser_download_url=url??prefix+name,size,digest};
        object Release(object[] assets,bool draft=false,bool prerelease=false,string date="2026-10-30T12:00:00Z")=>new{tag_name="pack-v1",draft,prerelease,published_at=date,assets};
        async Task<PackManifest?> Resolve(object[] releases,PackManifest? payload=null)
        {
            using var http=new HttpClient(new Handler(request=>JsonSerializer.Serialize(request.RequestUri!.Host=="api.github.com"?(object)releases:payload??manifest,PackManifest.Json)));
            return await new GitHubReleases(new Transfer(http)).Latest(default);
        }
        await Test("no releases and JAR/launcher-only releases are not full packs",async()=>{
            Assert(await Resolve([])==null);Assert(await Resolve([Release([Asset("launcher.zip"),Asset("mod.jar")])])==null);
        });
        await Test("matching first-party assets resolve automatically",async()=>{
            var found=await Resolve([Release([Asset(GitHubReleases.MetadataAsset),Asset(GitHubReleases.ArchiveAsset,digest:"sha256:"+new string('a',64))])]);Assert(found?.Archive==manifest.Archive);
        });
        await Test("draft and prerelease packs ignored",async()=>{
            var assets=new[]{Asset(GitHubReleases.MetadataAsset),Asset(GitHubReleases.ArchiveAsset)};
            Assert(await Resolve([Release(assets,true),Release(assets,prerelease:true)])==null);
        });
        await Test("foreign metadata or archive cannot redirect pack source",async()=>{
            await Reject(()=>Resolve([Release([Asset(GitHubReleases.MetadataAsset,"https://evil.test/metadata"),Asset(GitHubReleases.ArchiveAsset)])]));
            await Reject(()=>Resolve([Release([Asset(GitHubReleases.MetadataAsset),Asset(GitHubReleases.ArchiveAsset)])],manifest with{Archive=manifest.Archive! with{Url="https://evil.test/pack.zip"}}));
        });
        await Test("wrong asset digest/size rejected",async()=>{
            await Reject(()=>Resolve([Release([Asset(GitHubReleases.MetadataAsset),Asset(GitHubReleases.ArchiveAsset,digest:"sha256:"+new string('c',64))])]));
            await Reject(()=>Resolve([Release([Asset(GitHubReleases.MetadataAsset),Asset(GitHubReleases.ArchiveAsset,size:124)])]));
        });
        await Test("missing, duplicate or oversized metadata rejected",async()=>{
            await Reject(()=>Resolve([Release([Asset(GitHubReleases.MetadataAsset)])]));
            await Reject(()=>Resolve([Release([Asset(GitHubReleases.MetadataAsset),Asset(GitHubReleases.MetadataAsset),Asset(GitHubReleases.ArchiveAsset)])]));
            await Reject(()=>Resolve([Release([Asset(GitHubReleases.MetadataAsset,size:1048577),Asset(GitHubReleases.ArchiveAsset)])]));
        });
        Assert(passed==6);Console.WriteLine("PASS: 6 GitHub release scenarios");
    }
    public static async Task Live(string configPath)
    {
        var config=AccountsConfig.Load(configPath);if(!config.Enabled)throw new Exception("Live accounts must be configured");
        using var http=new HttpClient(new HttpClientHandler{AllowAutoRedirect=false,UseCookies=false});http.DefaultRequestHeaders.Add("apikey",config.PublicKey);
        using(var response=await http.GetAsync(config.Url+"/auth/v1/settings"))
        {response.EnsureSuccessStatusCode();using var data=JsonDocument.Parse(await response.Content.ReadAsStringAsync());if(data.RootElement.GetProperty("disable_signup").GetBoolean()||data.RootElement.GetProperty("mailer_autoconfirm").GetBoolean())throw new Exception("Signup/confirmation settings mismatch");}
        using(var response=await http.GetAsync(config.Url+"/rest/v1/profiles?select=id,nickname"))
        {if(response.StatusCode is not (HttpStatusCode.Unauthorized or HttpStatusCode.Forbidden))throw new Exception("Anonymous profile read must be denied");}
        using(var response=await http.PostAsJsonAsync(config.Url+"/rest/v1/profiles",new{id=Guid.NewGuid(),nickname="ForbiddenUser"}))
        {if(response.StatusCode is not (HttpStatusCode.Unauthorized or HttpStatusCode.Forbidden))throw new Exception("Anonymous profile insert must be denied");}
        using(var response=await http.GetAsync(config.Url+"/functions/v1/account-confirmed"))response.EnsureSuccessStatusCode();
        using var releasesHttp=Transfer.CreateClient();await new GitHubReleases(new Transfer(releasesHttp)).Latest(default);
        Console.WriteLine("PASS: live Supabase public settings + anonymous RLS denials + confirmation landing + GitHub Releases API; real-user email/login NOT tested");
    }
    private sealed class Handler(Func<HttpRequestMessage,string> body):HttpMessageHandler
    {
        protected override Task<HttpResponseMessage> SendAsync(HttpRequestMessage request,CancellationToken ct){ct.ThrowIfCancellationRequested();return Task.FromResult(new HttpResponseMessage(HttpStatusCode.OK){Content=new StringContent(body(request),Encoding.UTF8,"application/json")});}
    }
}
