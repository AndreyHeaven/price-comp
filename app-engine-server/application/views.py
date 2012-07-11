import cgi
from flask import Response
from json import dumps
from application import app
from application.models import Good
from google.appengine.ext import db


def json_response(values):
    return Response(dumps(values), mimetype='application/json')

@app.route('/good/<id>')
def save_good():
    u = Good()
    u.price = 17.1
    u.latitude = 10.2
    u.longitude = 20.4
    u.put()

    return json_response({'save': ''})

@app.route('/good/<id>')
def fined_good(id):

    goods = Good.gql('')
#    db.GqlQuery("SELECT * "
#                                    "FROM Good ")
#                                    "WHERE ANCESTOR IS :1 "
#                                    "ORDER BY date DESC LIMIT 10",
#                                    guestbook_key(guestbook_name))
    v = []
    for good in goods:
       v.append({'price':good.price,'lat':good.latitude,'log':good.longitude})
    return json_response({'name':'','prices':v})

#    return json_response({"name":id,
#    "prices":[
#     {
#     "price":123,
#     "date":"2012-12-12",
#     "log":54.2344,
#     "lat":34.234,
#     "r":10
#     },
#     {
#     "price":123,
#     "date":"2012-12-12",
#     "log":54.2344,
#     "lat":34.234,
#     "r":10
#     },
#     {
#     "price":123,
#     "date":"2012-12-12",
#     "log":54.2344,
#     "lat":34.234,
#     "r":10
#     },
#     {
#     "price":123,
#     "date":"2012-12-12",
#     "log":54.2344,
#     "lat":34.234,
#     "r":10
#     },
#     {
#     "price":123,
#     "date":"2012-12-12",
#     "log":54.2344,
#     "lat":34.234,
#     "r":10
#     },
#     {
#     "price":123,
#     "date":"2012-12-12",
#     "log":54.2344,
#     "lat":34.234,
#     "r":10
#     },
#     {
#     "price":123,
#     "date":"2012-12-12",
#     "log":54.2344,
#     "lat":34.234,
#     "r":10
#     }
#    ]})

def warmup():
    """App Engine warmup handler
    See http://code.google.com/appengine/docs/python/config/appconfig.html#Warming_Requests
    """
    return ''

