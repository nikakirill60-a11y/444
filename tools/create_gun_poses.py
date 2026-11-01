#!/usr/bin/env python3
"""Explicit 3D poses, in metres AFTER native geometry scale (not flat-icon transforms)."""
import json
from import_content import ASSETS, write_json

def main():
    catalogue = json.loads((ASSETS / 'geometry/catalogue.json').read_text())
    # Native meshes all point +X. Their barrel-centre Z differs between source models.
    # The former item/generated rotations assumed a flat XY icon and pointed them sideways.
    grips = {
        'ak74':[-.04,.10,-.175], 'aks74u':[-.01,.08,-.175], 'duplet':[-.17,.13,-.18],
        'rpk':[-.02,.07,-.175], 'saiga':[-.08,.10,-.20], 'tihar':[-.07,.16,-.1875],
        'vsv':[-.20,.10,-.125], 'vintovka':[-.25,.06,-.175], 'pkm':[-.05,.10,-.24],
        'podonok':[.13,.08,-.175], 'preved':[-.08,.10,-.195], 'revolver':[.08,.08,-.175],
        'svd':[-.12,.13,-.2625], 'vssk':[-.08,.14,-.20], 'ybludok':[0,.10,-.19],
        'yboinik':[-.08,.09,-.175]}
    # Under-foregrip palm centres, checked against transformed part bounds.
    # 1.10 lowers/retracts oversized overhand supports; revolver stays byte-for-byte unchanged.
    contacts = {
        'ak74': ([-0.115, 0.115, -0.175], [0.4, 0.19, -0.175]),
        'aks74u': ([-0.105, 0.115, -0.175], [0.28, 0.21, -0.175]),
        'duplet': ([-0.285, 0.22, -0.208], [0.13, 0.21, -0.208]),
        'rpk': ([-0.115, 0.115, -0.175], [0.38, 0.19, -0.175]),
        'saiga': ([-0.125, 0.135, -0.2], [0.42, 0.255, -0.2]),
        'tihar': ([-0.085, 0.145, -0.1875], [0.22, 0.18, -0.1875]),
        'vsv': ([-0.155, 0.22, -0.165], [0.3, 0.2, -0.165]),
        'vintovka': ([-0.185, 0.105, -0.175], [0.26, 0.195, -0.175]),
        'pkm': ([-0.05, 0.115, -0.185], [0.5, 0.18, -0.24]),
        'podonok': ([0.115, 0.085, -0.175], [0.36, 0.21, -0.175]),
        'preved': ([-0.145, 0.125, -0.195], [0.5, 0.17, -0.195]),
        'revolver': ([.05,.14,-.175], [.12,.19,-.175]),
        'svd': ([-0.135, 0.205, -0.19], [0.5, 0.215, -0.19]),
        'vssk': ([-0.06, 0.225, -0.2], [0.3, 0.215, -0.2]),
        'ybludok': ([-0.015, 0.105, -0.19], [0.26, 0.22, -0.19]),
        'yboinik': ([-0.11, 0.115, -0.175], [0.27, 0.105, -0.175]),
    }
    poses = {}
    for name, info in catalogue.items():
        if info['kind'] != 'gun':
            continue
        poses[name] = {
            'grip': grips[name],
            'trigger': contacts[name][0],
            'support': contacts[name][1],
            'firstPerson': {'translation':[-.20,.08,-.25] if name=='revolver' else [-.16,.015,-.34], 'rotation':[0,100,0], 'scale':.8 if name=='revolver' else .75},
            # HeldItemLayer already turns the arm -90 X / 180 Y. +X -> +Y here
            # makes the muzzle face forward after those parent transforms.
            'thirdPerson': {'translation':[0,.05,.10], 'rotation':[0,0,90], 'scale':.65},
            'ground': {'translation':[0,.12,0], 'rotation':[0,0,0], 'scale':.30},
            'fixed': {'translation':[0,0,0], 'rotation':[0,0,0], 'scale':.28}}
    write_json(ASSETS / 'geometry/gun_poses.json', poses)

if __name__ == '__main__':
    main()
