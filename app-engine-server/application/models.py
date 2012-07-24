from google.appengine.ext import db
import geo.geomodel
class Good(db.Model):
    code = db.StringProperty()

class Store(geo.geomodel.GeoModel):
    name = db.StringProperty()
    date = db.DateProperty()

class Price(db.Model):
    good = db.ReferenceProperty(Good)
    price = db.FloatProperty()
    user = db.StringProperty()
    date = db.DateProperty()
    store = db.ReferenceProperty(Store)

