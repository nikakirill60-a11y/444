using System.Net;
using System.Text;
using MinETRO.Core;
namespace MinETRO.Launcher;
internal static class Program
{
    [STAThread]
    static int Main(string[] args)
    {
        ApplicationConfiguration.Initialize();bool smoke=args.Contains("--self-test");
        try
        {
            var manifest=PackManifest.ReleaseDefault;
            // No cached GitHub manifest may unlock dev or public channels.
            // Smoke runs use only a mock transport, never a production user or a bypass in the normal app.
            AccountPage? page=smoke?new AccountPage(new("https://ui-smoke.supabase.co","sb_publishable_ui_smoke",false),new SmokeAccounts()):null;
            using var form=new MainForm(manifest,page,!smoke);
            if(!smoke){Application.Run(form);return 0;}
            int code=1;
            form.Shown+=async(_,_)=>
            {
                try{await form.SmokeTest(args.Length>1?args[1]:null);code=0;}
                catch(Exception ex){if(args.Length>1)File.WriteAllText(args[1]+".error.txt",ex.ToString());}
                finally{form.Close();}
            };
            Application.Run(form);return code;
        }
        catch(Exception ex){if(!smoke)MessageBox.Show(ex.Message,"MinETRO · запуск");else if(args.Length>1)File.WriteAllText(args[1]+".error.txt",ex.ToString());return 1;}
    }
    private sealed class SmokeAccounts : HttpMessageHandler
    {
        private bool requested; private int statusReads;
        protected override Task<HttpResponseMessage> SendAsync(HttpRequestMessage request,CancellationToken ct)
        {
            ct.ThrowIfCancellationRequested();
            if(request.RequestUri!.AbsolutePath=="/rest/v1/rpc/request_dev_access")requested=true;
            if(request.RequestUri.AbsolutePath=="/rest/v1/dev_access_requests" && requested)statusReads++;
            string data=request.RequestUri!.AbsolutePath switch
            {
                "/auth/v1/token" or "/auth/v1/verify"=>"{\"access_token\":\"ui-smoke-not-real\",\"refresh_token\":\"ui-smoke-refresh-not-real\",\"expires_in\":3600,\"user\":{\"id\":\"11111111-1111-4111-8111-111111111111\"}}",
                "/rest/v1/profiles"=>"[{\"id\":\"11111111-1111-4111-8111-111111111111\",\"nickname\":\"PreviewPlayer\",\"skin_path\":null,\"skin_model\":\"steve\"}]",
                "/auth/v1/logout"=>"{}",
                "/rest/v1/dev_access_requests"=>!requested?"[]":statusReads==1?"[{\"status\":\"pending\"}]":"[{\"status\":\"approved\"}]",
                "/rest/v1/rpc/request_dev_access"=>"\"pending\"",
                "/rest/v1/rpc/get_dev_release"=>"null",
                _=>throw new InvalidOperationException("Unexpected smoke network request")
            };
            return Task.FromResult(new HttpResponseMessage(HttpStatusCode.OK){Content=new StringContent(data,Encoding.UTF8,"application/json")});
        }
    }
}
