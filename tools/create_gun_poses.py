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
    # Palm centres measured against scaled/transformed meshes, NOT camera-pivot grips.
    # In 1.4.1 the pivot and a generic downward offset made the hands float below the gun.
    contacts = {
        'ak74': ([-.10,.13,-.175], [.50,.30,-.175]),
        'aks74u': ([-.09,.13,-.175], [.38,.24,-.175]),
        'duplet': ([-.27,.235,-.208], [.23,.32,-.208]),
        'rpk': ([-.10,.13,-.175], [.48,.29,-.175]),
        'saiga': ([-.11,.15,-.20], [.52,.30,-.20]),
        'tihar': ([-.07,.16,-.1875], [.32,.24,-.1875]),
        'vsv': ([-.14,.235,-.165], [.40,.34,-.165]),
        'vintovka': ([-.17,.12,-.175], [.36,.25,-.175]),
        'pkm': ([-.035,.13,-.185], [.47,.22,-.24]),
        'podonok': ([.13,.10,-.175], [.46,.25,-.175]),
        'preved': ([-.13,.14,-.195], [.60,.30,-.195]),
        'revolver': ([.05,.14,-.175], [.12,.19,-.175]),
        'svd': ([-.12,.22,-.19], [.60,.27,-.19]),
        'vssk': ([-.045,.24,-.20], [.40,.27,-.20]),
        'ybludok': ([0,.12,-.19], [.36,.29,-.19]),
        'yboinik': ([-.095,.13,-.175], [.37,.245,-.175]),
    }
    poses = {}
    for name, info in catalogue.items():
        if info['kind'] != 'gun':
            continue
        poses[name] = {
            'grip': grips[name],
            'trigger': contacts[name][0],
            'support': contacts[name][1],
            'firstPerson': {'translation':[-.20,.08,-.25], 'rotation':[0,100,0], 'scale':.8},
            # HeldItemLayer already turns the arm -90 X / 180 Y. +X -> +Y here
            # makes the muzzle face forward after those parent transforms.
            'thirdPerson': {'translation':[0,.05,.10], 'rotation':[0,0,90], 'scale':.65},
            'ground': {'translation':[0,.12,0], 'rotation':[0,0,0], 'scale':.30},
            'fixed': {'translation':[0,0,0], 'rotation':[0,0,0], 'scale':.28}}
    write_json(ASSETS / 'geometry/gun_poses.json', poses)

if __name__ == '__main__':
    main()
