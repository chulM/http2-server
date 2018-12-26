import sys
import socket
import ssl
import requests


domain = sys.argv[1]

headers = {'Accept' :'*/*', 'user-agent': 'h2-check/1.0.1', 'Connection' : 'Upgrade, Http2-Settings',
'Upgrade' : 'h2c', 'HTTP2-Settings':'<base64url encoding of HTTP/2 SETTINGS payload>'}


def checkH2 (domain):
    # send get request with the upgrade header
    r = requests.get('http://' + domain, headers=headers, allow_redirects=True)

    #check the status code 101 switching protocol base on http 1.1 frist
    if r.status_code == 101:
        print ('this domain support http/2 with h2c - http')
    # 200 dose not support http2/2
    else:
        print ('this domain not support http/2 with h2c - http')


def checkH2S (domain):
    ctx = ssl.create_default_context()
    ctx.set_alpn_protocols(['h2','spdy/3','http/1.1'])

    conn = ctx.wrap_socket(socket.socket(socket.AF_INET,socket.SOCK_STREAM),server_hostname=domain)
    conn.connect((domain,443))
    if conn.selected_alpn_protocol() == 'h2':
        print ('this domain support http/2 with h2c - https')
    else:
        print ('this domain not support http/2 with h2c - https but' + str(conn.selected_alpn_protocol()))


checkH2(domain)
checkH2S(domain)
