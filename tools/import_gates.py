#!/usr/bin/env python3
"""Recover the original gate meshes/atlases; never execute decompiled Java."""
from pathlib import Path
import re
import zipfile
from import_content import ROOT, ASSETS, put

MAPPING = {'field_78089_u':'textureHeight','field_78090_t':'textureWidth',
           'field_78795_f':'rotateAngleX','field_78796_g':'rotateAngleY','field_78808_h':'rotateAngleZ',
           'field_78809_i':'mirror','func_78785_a':'render','func_78787_b':'setTextureSize',
           'func_78789_a':'addBox','func_78793_a':'setRotationPoint'}
MODELS = ['ModelGermo', 'ModelGermo1', 'ModelGermoLeft', 'ModelGermoRamka']

def main():
    for name in MODELS:
        text = (ROOT / 'tools/legacy-reference/blocks/models' / (name + '.java')).read_text()
        # Discard unused entity adapters, including the legacy infinitely recursive overload.
        for signature in ['public void func_78088_a(', 'public void setRotationAngles(']:
            start = text.index(signature)
            end = text.index('{', start) + 1
            depth = 1
            while depth:
                depth += (text[end] == '{') - (text[end] == '}')
                end += 1
            text = text[:start] + text[end:]
        text = text.replace('package blocks.models;', 'package ru.metrocraft.port.client.gates;')
        text = '\n'.join(line.rstrip() for line in text.splitlines()).rstrip() + '\n'
        for source, target in MAPPING.items():
            text = text.replace(source, target)
        put(ROOT / 'src/main/java/ru/metrocraft/port/client/gates' / (name + '.java'), text)
    with zipfile.ZipFile(ROOT / '(Metro) Flans-v1.1.jar') as original:
        for name in ['model_techne', 'ModelGermo1', 'ModelGermoLeft', 'Germa']:
            put(ASSETS / 'textures/gates' / (name.lower() + '.png'),
                original.read('assets/sub_mod_techne_block/textures/blocks/' + name + '.png'))
        for name in ['Germa', 'GermaRamka']:
            put(ASSETS / 'models/legacy' / (name.lower() + '.obj'),
                original.read('assets/sub_mod_techne_block/models/' + name + '.obj'))
        put(ASSETS / 'sounds/germo.ogg', original.read('assets/sub_mod_techne_block/sounds/germo.ogg'))
    for name in ['germo', 'germo_open', 'germo_right', 'germo_left', 'germo_left45', 'germo_half_open']:
        put(ASSETS / 'models/item' / (name + '.json'), '{"parent":"minecraft:block/iron_block"}\n')
    print('Imported 4 original gate models, 2 OBJ meshes and 4 atlases')

if __name__ == '__main__':
    main()
