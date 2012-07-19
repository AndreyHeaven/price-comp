# coding=utf-8
import datetime
from flask import Response
from json import dumps
from flask import request
from application import app
from application.models import Good, Price, Store
from google.appengine.ext import db


def json_response(values):
    return Response(dumps(values), mimetype='application/json')

def add_price():
    body = request.values
    code = body.get('code', None)
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
            store = Store()
            store.name = body.get('name', None)
            store.latitude = int(body.get('latitude', None))
            store.longitude = int(body.get('longitude', None))
            store.accuracy = int(body.get('accuracy', None))
            store.put()

            new_price.good = good.key()
            new_price.price = float(body.get('price', None))
            new_price.date = datetime.date.today()
            new_price.store = store.key()
            new_price.put()

        except Exception:
            return return_error('incorrect value')

        return json_response({'message':'OK'})
    else:
        return return_error('invalid code !')


@app.route('/find_good/?id=<id>&lat=<lat>&long=<long>&acc=<acc>', methods = ['GET'])
def find_good(id, lat, long, acc):
    if id is not None and len(id) > 0:
        good = db.GqlQuery("SELECT * "
                        "FROM Good "
                        "WHERE code = :1 ",
                        id)
        if good.count() > 0:
            find_good = good[0]

            stores = get_array_of_stores(lat,long,acc)
            if stores is not None:

                prices = db.GqlQuery("SELECT * "
                            "FROM Price "
                            "WHERE good = :1 AND"
                            "store in( :2 )"
                            "ORDER BY price ASC LIMIT 10",
                            find_good.key(), stores)

                array_of_prices = []
                for price in prices:
                    array_of_prices.append({'price':price.price,'date':price.date.strftime("%Y-%m-%d"),'store':price.store.name,'lat':price.store.latitude,'log':price.store.longitude})
                return json_response({'code':id,'prices':array_of_prices})
            else:
                return return_error('stores not found !')
        else:
            return json_response({'code':id})
    else:
        return return_error('invalid code !')


@app.route('/stores/?lat=<lat>&long=<long>&acc=<acc>', methods = ['GET'])
def get_stores(lat,long,acc):
    if lat & long is not None and len(id) > 0:
        if acc is None:
            acc = 500 #Hack
        stores = get_array_of_stores(lat,long,acc)
        if stores is not None:
            array_of_stores = []
            for find_stores in stores[0]:
                array_of_stores.append({'name':find_stores.price,'lat':find_stores.latitude,'log':find_stores.longitude,'date':find_stores.date.strftime("%Y-%m-%d")})
            return json_response({'stores':array_of_stores})
        else:
            return return_error('stores not found !')
    else:
        return return_error('invalid lat or long !')


def add_store():
    body = request.values
    stores = get_array_of_stores(body.get('latitude', None), body.get('longitude', None), body.get('accuracy', None))
    for store in stores:
        if store.name == body.get('name'):
            return return_error('can\'t add store, store with name already exist!')
    try:
        store = Store()
        store.name = body.get('name', None)
        store.latitude = body.get('latitude', None)
        store.longitude = body.get('longitude', None)
        store.date = datetime.date.today()
        store.put()
    except Exception:
        return return_error('incorrect value')
    return json_response({'message':'OK'})

def get_array_of_stores(lat,long,acc):
    lat = int(lat)
    long = int(long)
    acc = int(acc)
    stores = db.GqlQuery("""SELECT *
                           FROM Store
                           WHERE latitude > :1 AND
                           latitude < :2 AND
                           longitude > :3 AND
                           longitude < :4
                           ORDER BY name ASC LIMIT 10""",
                           lat-acc, lat+acc, long-acc, long+acc)
    return stores

def return_error(error_message):
    return json_response({'error': error_message})

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

