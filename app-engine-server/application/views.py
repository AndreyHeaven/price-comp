# coding=utf-8
import cgi
import uuid
import datetime
from flask import Response
from json import dumps
from flask import request
import time
from application import app
from application.models import Good, Price
from google.appengine.ext import db


def json_response(values):
    return Response(dumps(values), mimetype='application/json')

#@app.route('/add_price/<id>')
def add_price():
    body = request.values
    code = body.get('code', None)
#    form = request.values
    if code is not None and len(code) > 0:
        new_price = Price()
        persistent_good = db.GqlQuery("SELECT * "
                        "FROM Good "
                        "WHERE code = :1 ",
                        code)
        if persistent_good.count() > 0:
            good = persistent_good[0]
        else:
            good = Good()
            good.code = code
            good.put()
        try:
            new_price.good = good.key()
            new_price.price = float(body.get('price', None))
            new_price.latitude = float(body.get('latitude', None))
            new_price.longitude = float(body.get('longitude', None))
            new_price.accuracy = float(body.get('accuracy', None))
            new_price.date = datetime.date.today()
            new_price.put()
        except Exception:
            return json_response({'error':'incorrect value'})

        return json_response({'message':'OK'})
    else:
        return json_response({'error': 'invalid code !'})

@app.route('/good/<id>')
def fined_good(id):
    if id is not None and len(id) > 0:
        good = db.GqlQuery("SELECT * "
                        "FROM Good "
                        "WHERE code = :1 ",
                        id)
        if good.count() > 0:
            fined_good = good[0]

            prices = db.GqlQuery("SELECT * "
                        "FROM Price "
                        "WHERE good = :1 "
                        "ORDER BY date DESC LIMIT 10",
                        fined_good.key())

            array_of_prices = []
            for price in prices:
                array_of_prices.append({'price':price.price,'lat':price.latitude,'log':price.longitude,'accu':price.accuracy})
            return json_response({'code':id,'prices':array_of_prices})
        else:
            return json_response({'error': 'not fount code !'})
    else:
           return json_response({'error': 'invalid code !'})

@app.route('/')
def main_page():
    return """
<html>
заглушка
</html>
    """

def warmup():
    """App Engine warmup handler
    See http://code.google.com/appengine/docs/python/config/appconfig.html#Warming_Requests
    """
    return ''

