"""Native OBJ-to-hand fitting. Contacts are measured in SOURCE metres, not legacy Flan wrists.

+Z is the muzzle for every complete assembly (including Gatling/stvol.obj). The model is fitted
around its actual rear handle. Support contacts describe the underside surface;
the actual fingertip pad is pinned to it, so the fist neither floats nor swallows the receiver.
The independent camera scale must not be used to fix the weapon/hand size ratio.
"""
SCENE_BODIES={'AK':'ak/ak.obj','AK2012':'2012/2012.obj','AKSU':'aksu/aksu.obj',
 'Revolver':'revolver/revolver.obj','Rpk':'rpk/rpk.obj','Saiga':'saiga/saiga.obj','Ubludok':'ubludok/ubludok.obj'}
GUN_MODELS={'Gatling':'gatling/gatling.obj','Abzac':'abzac/abzac.obj',
 'Vyhlop':'vyhlop/vyhlop1.obj','Velopal':'vyhlop/velo.obj','Ognemet':'ognemet/ognemet1.obj',**SCENE_BODIES}
# scale, rear handle centre, support underside, iron-sight line, muzzle sign
# These are intentionally per-weapon: AK2012's bullpup grip, Velopal's sloping
# handle and Gatling's off-centre grip are not interchangeable AK contacts.
FITS={
 'Gatling':(1.35,[-.075,-.075,-.12],[0,-.093400,.055],[0,.085,.35],1),
 'Abzac':(1.40,[-.079,-.080,-.255],[0,-.058500,.34],[0,.085,.15],1),
 'Vyhlop':(1.55,[0,-.075,-.035],[0,-.035267,.16],[0,.055,.15],1),
 'Velopal':(1.55,[0,-.150,-.32],[0,-.115009,.04],[0,.075,.12],1),
 'Ognemet':(1.35,[-.079,-.085,-.22],[.125,-.070943,.26],[0,.070,.34],1),
 'AK':(1.65,[0,-.085,-.216],[0,-.021501,.12],[0,.060,.25],1),
 'AK2012':(1.65,[-.025,-.085,.0],[-.025,-.028398,.13],[-.025,.080,.18],1),
 'AKSU':(1.65,[0,-.085,-.216],[0,-.025081,.075],[0,.060,.15],1),
 'Revolver':(1.60,[0,.015,-.09],[0,.022702,-.055],[0,.120,.12],1),
 'Rpk':(1.65,[0,-.085,-.216],[0,-.020838,.14],[0,.060,.28],1),
 'Saiga':(1.65,[0,-.085,-.216],[0,-.026743,.14],[0,.055,.25],1),
 'Ubludok':(1.65,[0,-.080,-.070],[0,-.049737,.22],[0,.040,.25],1),
}

def fitted_point(name,point):
 scale,grip,_,_,sign=FITS[name]
 return [round(sign*(point[2]-grip[2])*scale,6),round((point[1]-grip[1])*scale,6),round(-sign*(point[0]-grip[0])*scale,6)]

def models():
 result={}
 for name,body in GUN_MODELS.items():
  scale,grip,_,_,sign=FITS[name]
  result[name]=dict(id='blackrazid_'+name.lower(),sourceId=name,body=body,
      nativeTranslation=[round(-sign*grip[2]*scale,6),round(-grip[1]*scale,6),round(sign*grip[0]*scale,6)],
      nativeScale=scale,nativeYaw=90*sign)
 return result

def poses():
 result={}
 for name in GUN_MODELS:
  _,_,surface,sight,_=FITS[name]
  support=fitted_point(name,surface)
  result['blackrazid_'+name.lower()]=dict(grip=[0,0,0],trigger=[0,0,0],support=support,aimSight=fitted_point(name,sight),
      firstPerson=dict(translation=[-.20,.10,-.50],rotation=[0,100,0],scale=.8),
      thirdPerson=dict(translation=[0,.05,.1],rotation=[0,0,90],scale=.65),
      ground=dict(translation=[0,.12,0],rotation=[0,0,0],scale=.3),
      fixed=dict(translation=[0,0,0],rotation=[0,0,0],scale=.28))
 return result
