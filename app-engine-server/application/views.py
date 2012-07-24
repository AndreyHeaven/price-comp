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
            storeId = body.get('store', None)
            if storeId is None:
                return return_error('please select store')
            storeId = int(storeId)
            store = Store.get_by_id(storeId)
            new_price.good = good
            new_price.user = body.get('user', None)
            new_price.price = float(body.get('price', None))
            new_price.date = datetime.date.today()
            new_price.store = store.key()
            new_price.put()

        except Exception:
            return return_error('incorrect value')

        return json_response({'message': 'OK'})
    else:
        return return_error('invalid code !')


@app.route('/good/<barcode>/<lat>/<long>/<acc>', methods=['GET'])
def find_good(barcode, lat, long, acc):
    if barcode is not None and len(barcode) > 0:
        good = db.GqlQuery("SELECT * "
                           "FROM Good "
                           "WHERE code = :1 ",
            barcode).fetch(1)
        if len(good) > 0:
            good = good[0]

            stores = get_array_of_stores(lat, long, acc)
            if len(stores) > 0:
                prices = Price.all().filter('good = ',good).filter('store in ',stores).order('price')

                array_of_prices = []
                for price in prices:
                    try:
                        obj1 = price.store
                        array_of_prices.append(
                                {'price': price.price, 'date': price.date.strftime("%Y-%m-%d"),
                                 'store': price.store.name,
                                 'lat': obj1.location.lat, 'lon': obj1.location.lon})
                    except db.ReferencePropertyResolveError:
                        # Referenced entity was deleted or never existed.
                        pass
                return json_response({'code': good.code, 'id': good.key().id(), 'prices': array_of_prices})
            else:
                return return_error('stores not found !')
        else:
            return json_response({'code': barcode})
    else:
        return return_error('invalid code !')


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
            return return_error('stores not found !')
    else:
        return return_error('invalid lat or long !')


@app.route('/store/', methods=['PUT'])
def add_store():
    body = request.values
    latitude = body.get('lat', None)
    longitude = body.get('lon', None)
    name = body.get('name', None)
    logging.debug("Store name %s and encoding %s", name, str(type(name)))
    stores = get_array_of_stores(latitude, longitude)
    for store in stores:
        if store.name == body.get('name'):
            return return_error('can\'t add store, store with name already exist!')
    try:
        store = Store(location=db.GeoPt(int(latitude) / 1e6, int(longitude) / 1e6),
            name=name,
            date=datetime.date.today())
        store.update_location()
        store.put()
    except Exception:
        return return_error('incorrect value')
    return json_response({'message': 'OK'})


def get_array_of_stores(lat, long, acc=500):
    lat = int(lat)
    long = int(long)
    acc = int(acc)

    stores = Store.proximity_fetch(Store.all(), db.GeoPt(lat / 1e6, long / 1e6),
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

