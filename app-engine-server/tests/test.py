# coding=utf-8
__author__ = 'araigorodskiy'
import urllib
import urllib2
opener = urllib2.build_opener(urllib2.HTTPHandler)
app_url = 'http://localhost:8080/'

def add_store():
    body = {
    #        'code':'666'
    'name':'Магнит'
    #        ,'price':'30'
    ,'accuracy':'20'
    ,'lon':46006471
    ,'lat':51548459}
    request = urllib2.Request('http://localhost:8080/store/', data=urllib.urlencode(body))
    #request.add_header('Content-Type', 'your/contenttype')
    request.get_method = lambda: 'PUT'
    url = opener.open(request)
    print(url)


def get_stores():
    request = urllib2.Request(app_url+'/stores/?lat=46&long=51&acc=10000')
    #request.add_header('Content-Type', 'your/contenttype')
    request.get_method = lambda: 'GET'
    url = opener.open(request)
    print url.read()

# Use App Engine app caching
if __name__ == "__main__":
    add_store()
