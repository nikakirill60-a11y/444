"""Selected native mesh assemblies. Hand-space conversion/poses are explicit 1.12 adaptations."""
GUN_MODELS={
 'Gatling':('gatling/gatling.obj',[.12,.164,-.126],.7,[.12,.20,-.32]),
 'Abzac':('abzac/abzac.obj',[.05,.17,-.17],.8,[.30,.14,-.175]),
 'Vyhlop':('vyhlop/vyhlop1.obj',[.075,.175,-.17],.8,[.28,.145,-.175]),
 'Velopal':('vyhlop/velo.obj',[.10,.175,-.17],.8,[.19,.145,-.175]),
 'Ognemet':('ognemet/ognemet1.obj',[.05,.17,-.17],.7,[.29,.145,-.175]),
}
def models():
 return {name:dict(id='blackrazid_'+name.lower(),sourceId=name,body=body,nativeTranslation=translation,nativeScale=scale) for name,(body,translation,scale,support) in GUN_MODELS.items()}
def poses():
 return {'blackrazid_'+name.lower():dict(grip=[-.04,.1,-.175],trigger=[-.075,.115,-.175],support=support,
    firstPerson=dict(translation=[-.18,-.045,-.40],rotation=[0,100,0],scale=.8),
    thirdPerson=dict(translation=[0,.05,.1],rotation=[0,0,90],scale=.65),
    ground=dict(translation=[0,.12,0],rotation=[0,0,0],scale=.3),
    fixed=dict(translation=[0,0,0],rotation=[0,0,0],scale=.28)) for name,(body,translation,scale,support) in GUN_MODELS.items()}
