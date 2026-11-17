// Deployed to project xmhxucddhkitxqtweifu, function account-confirmed, verify_jwt=false.
// Public read-only landing only. Supabase Auth, not this endpoint, verifies the email.
Deno.serve((request: Request) => {
  if (request.method !== 'GET' && request.method !== 'HEAD') return new Response('Method not allowed', { status: 405 });
  // Never read, store or log URL tokens. Browser fragments are not sent to this endpoint.
  return new Response(request.method === 'HEAD' ? null : 'MinETRO\n\nЕсли ты перешёл сюда по ссылке подтверждения почты, вернись в лаунчер и выбери «Вход».\n\nПри ошибке подтверждения запроси новое письмо в лаунчере.\nНе отправляй никому ссылки и коды из писем.', { headers: { 'Content-Type': 'text/plain; charset=utf-8', 'Cache-Control': 'no-store', 'Referrer-Policy': 'no-referrer', 'X-Content-Type-Options': 'nosniff' } });
});
