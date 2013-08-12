from google.appengine.ext import ndb
import geo.geomodel


class Good(ndb.Model):
    code = ndb.StringProperty()

    @classmethod
    def find_by_code(cls, code):
        return cls.query(cls.code == code)


class Store(geo.geomodel.GeoModel, ndb.Model):
    name = ndb.StringProperty()
    date = ndb.DateProperty(auto_now_add=True)
    code = ndb.StringProperty(indexed=True)

    @classmethod
    def find_by_code(cls, code):
        return cls.query(cls.code == code)


class Price(ndb.Model):
    good = ndb.KeyProperty(Good)
    price = ndb.FloatProperty()
    user = ndb.StringProperty()
    date = ndb.DateProperty(auto_now_add=True)
    store = ndb.KeyProperty(Store)

