# -*- coding: utf-8 -*-
import logging
import datetime
from flask import Response
from json import dumps
from flask import request
from application import app
from application.models import Good, Price, Store
from google.appengine.ext import db


def json_response(values):
    return Response(dumps(values), mimetype='application/json')


@app.route('/price/', methods=['PUT'])
def add_price():
    body = request.values
    code = body.get('code', None)
    if code is not None and len(code) > 0:

        good = Good.find_by_code(code).get()
        if good == None:
            good = Good()
            good.code = code
            good.put()

        try:
            storeId = body.get('osm_id', None)
            if storeId is None:
                return return_error({'id': 503, 'msg': 'please select store'})

            store = Store.find_by_code(storeId).get()
            if store == None:
                latitude = body.get('lat', None)
                longitude = body.get('lon', None)
                name = body.get('store', None)

                store = Store(name=name,
                              location=db.GeoPt(int(latitude) / 1e6, int(longitude) / 1e6))
                store.update_location()
                store.put()
            new_price = Price()
            new_price.good = good.key
            new_price.store = store.key
            new_price.user = body.get('user', None)
            new_price.price = float(body.get('price', None))
            new_price.put()

        except Exception as e:
            return return_error({'id': 500, 'msg': 'incorrect value'})

        return json_response({'message': 'OK'})
    else:
        return return_error({'id': 502, 'msg': 'invalid code !'})


@app.route('/good/<barcode>/<lat>/<lng>/<acc>', methods=['GET'])
def find_prices(barcode, lat, lng, acc):
    if barcode is not None and len(barcode) > 0:
        stores = get_array_of_stores(lat, lng, acc)
        if len(stores) > 0:
            good = db.GqlQuery("SELECT * "
                               "FROM Good "
                               "WHERE code = :1 ",
                               barcode).fetch(1)
            if len(good) > 0:
                good = good[0]
                prices = Price.query(Price.good == good, Price.store.IN(stores)).order(Price.price)

                array_of_prices = []
                array_of_stores = []
                stores_with_price = list()
                for store in stores:
                    array_of_stores.append(
                        {'id': store.key().id(), 'name': store.name, 'lat': store.location.lat * 1e6,
                         'lon': store.location.lon * 1e6,
                         'date': store.date.strftime("%Y-%m-%d")})
                for price in prices:
                    try:
                        obj1 = price.store
                        array_of_prices.append(
                            {'price': price.price, 'date': price.date.strftime("%Y-%m-%d"),
                             'store': obj1.key().id()})
                        stores_with_price.append(obj1.key().id())
                    except db.ReferencePropertyResolveError:
                        # Referenced entity was deleted or never existed.
                        pass
                array_of_stores = filter(lambda a: a['id'] in stores_with_price, array_of_stores)
                return json_response({'code': good.code, 'id': good.key().id(), 'stores': array_of_stores,
                                      'prices': array_of_prices})
            else:
                return json_response({})
        else:
            return json_response({})
    else:
        return return_error({'id': 502, 'msg': 'invalid code !'})


@app.route('/store/<lat>/<lon>/<acc>', methods=['GET']) #?lat=<lat>&long=<long>&acc=<acc>
def get_stores(lat, lon, acc=500):
    if lat is not None and lon is not None:
        stores = get_array_of_stores(lat, lon, acc)
        if stores is not None:
            array_of_stores = []
            for find_stores in stores:
                array_of_stores.append(
                    {'id': find_stores.key().id(), 'name': find_stores.name, 'lat': find_stores.location.lat * 1e6,
                     'lon': find_stores.location.lon * 1e6,
                     'date': find_stores.date.strftime("%Y-%m-%d")})
            return json_response(array_of_stores)
        else:
            return return_error({'id': 401, 'msg': 'stores not found !'})
    else:
        return return_error({'id': 501, 'msg': 'invalid lat or long !'})


@app.route('/store/', methods=['PUT'])
def add_store():
    body = request.values
    latitude = body.get('lat', None)
    longitude = body.get('lon', None)
    name = body.get('name', None)
    osm_id = body.get('osm', None)
    stores = get_array_of_stores(latitude, longitude)
    for store in stores:
        if store.osm_id == osm_id:
            return json_response({})

    for store in stores:
        if store.name == body.get('name'):
            return return_error({'id': 301, 'msg': 'can\'t add store, store with name already exist!'})

    try:
        store = Store(location=db.GeoPt(int(latitude) / 1e6, int(longitude) / 1e6),
                      name=name,
                      date=datetime.date.today())
        store.update_location()
        store.put()
    except Exception:
        return return_error({'id': 500, 'msg': 'incorrect value'})
    return json_response({'message': 'OK'})


def get_array_of_stores(lat, long, acc=500):
    lat = int(lat)
    long = int(long)
    acc = int(acc)

    stores = Store.proximity_fetch(Store.query(), db.GeoPt(lat / 1e6, long / 1e6),
                                   max_results=10,
                                   max_distance=acc + 1000)
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

