from google.appengine.ext import db

class Good(db.Model):
    code = db.StringProperty()

class Store(db.Model):
    name = db.StringProperty()
    pt = db.GeoPtProperty()
    longitude = db.IntegerProperty()
    latitude = db.IntegerProperty()
    date = db.DateProperty()

class Price(db.Model):
    good = db.ReferenceProperty(Good)
    price = db.FloatProperty()
    date = db.DateProperty()
    store = db.ReferenceProperty(Store)

