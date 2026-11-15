#!/usr/bin/env python3
r"""Collect direct model dependencies of donor functional decoration only.

Python 3, no dependencies. Pass a LOCAL Metro2033Models.jar:
  py -3 collect_metromod_decor.py "C:\Metro\content\Metro2033Models.jar"

Creates NEW metro_decor_parts folder, ZIP parts <=90 MiB and SHA256 inventory.
Selects the 112 catalogued Germo/Lever/Door/Light/Inventory entries and their
190 direct model references. Does not execute Java, download a server, install
a mod, collect vehicles/weapons or infer missing textures inside model files.
Original encrypted bytes, if present, are retained. NOT a playable decor port.
"""
import argparse
import hashlib
import json
from pathlib import Path, PurePosixPath
import stat
import zipfile

MODEL_REFERENCES = ('_add1.m2033model', '_add2.m2033model', 'arsenal/ammo_box.m2033model', 'arsenal/armorset_soldier_01.m2033model', 'arsenal/armorset_soldier_02.m2033model', 'arsenal/box_ak74.m2033model', 'arsenal/box_patron.m2033model', 'arsenal/gunparts_01.m2033model', 'arsenal/rukzak.m2033model', 'arsenal/shotgun_parts.m2033model', 'arsenal/wpn_support.m2033model', 'box/box_books.m2033model', 'box/box_books_broken.m2033model', 'box/box_metal.m2033model', 'box/box_metal1.m2033model', 'box/box_metal1_cap.m2033model', 'box/box_metal2.m2033model', 'box/box_metal2_cap.m2033model', 'box/box_metal3.m2033model', 'box/box_metal3_cap.m2033model', 'box/box_metal4.m2033model', 'box/box_metal4_cap.m2033model', 'box/box_metal5.m2033model', 'box/box_metal5_cap.m2033model', 'box/box_metal_4.m2033model', 'box/box_metal_4_cap.m2033model', 'box/box_metal_5.m2033model', 'box/box_metal_5_cap.m2033model', 'box/box_metal_cap.m2033model', 'box/test_instrumentbox_01.m2033model', 'box/test_instrumentbox_01_cap.m2033model', 'columnes/round_column_svetilnik.m2033model', 'constr_metal/grate1_door.m2033model', 'decor_metro/fonar_01.m2033model', 'decor_metro/karbid_lamp_01.m2033model', 'decor_metro/karbid_lamp_02.m2033model', 'decor_metro/l07_vorota.m2033model', 'decor_metro/l07_vorota_add1.m2033model', 'decor_metro/l07_vorota_add2.m2033model', 'decor_metro/l07_vorota_add3.m2033model', 'decor_metro/lamp_abajur_2.m2033model', 'decor_metro/lamp_abajur_2_add.m2033model', 'decor_metro/lamp_halogen_1.m2033model', 'decor_metro/lamp_industrt01_01.m2033model', 'decor_metro/lamp_kerosinka_06_nice.m2033model', 'decor_metro/lamp_kerosinka_07_nice.m2033model', 'decor_metro/lamp_na_stenu_01.m2033model', 'decor_metro/lamp_nastolny_01.m2033model', 'decor_metro/lamp_nastolny_01_add1.m2033model', 'decor_metro/lamp_nastolny_01_add2.m2033model', 'decor_metro/lamp_projector_no_lp.m2033model', 'decor_metro/lamp_projector_no_lp_add.m2033model', 'decor_metro/lamp_small.m2033model', 'decor_metro/lamp_small_add.m2033model', 'decor_metro/lampa.m2033model', 'decor_metro/wpn_proj_01.m2033model', 'decor_metro/wpn_proj_02.m2033model', 'door/biblio_door_chit_zal_left.m2033model', 'door/biblio_door_chit_zal_right.m2033model', 'door/biblio_door_hall_razlom_left.m2033model', 'door/biblio_door_hall_razlom_right.m2033model', 'door/biblio_door_razlom.m2033model', 'door/d6_gate_platform_.m2033model', 'door/door_2_left.m2033model', 'door/door_2_right.m2033model', 'door/door_d06_l02_01.m2033model', 'door/door_d06_l02_01_add1.m2033model', 'door/door_hran.m2033model', 'door/door_l00_hospital.m2033model', 'door/door_l00_hospital_add1.m2033model', 'door/door_l00_hospital_add2.m2033model', 'door/l04_gate02_.m2033model', 'door/l04_gate02_add1.m2033model', 'door/l04_gate02_add2.m2033model', 'door/l04_gate02_add3.m2033model', 'door/l04_gate02_add4.m2033model', 'door/l04_t_zatvor.m2033model', 'door/l13_vorota_test.m2033model', 'door/l13_vorota_test_add1.m2033model', 'door/l13_vorota_test_add2.m2033model', 'door/l17_door_closed.m2033model', 'door/l17_door_closed_add1.m2033model', 'door/l17_door_gate.m2033model', 'door/l17_door_gate_add1.m2033model', 'door/l21_clap_door.m2033model', 'door/l21_clap_door_add1.m2033model', 'door/l21_level_doors02.m2033model', 'door/l21_level_doors02_add1.m2033model', 'door/l21_level_doors03.m2033model', 'door/l21_level_doors03_add1.m2033model', 'door/l21_level_doors04.m2033model', 'door/ostankino_door.m2033model', 'door/vorota.m2033model', 'door/vorota_add2.m2033model', 'door/vorota_add3.m2033model', 'electro/props_rubilnik_01.m2033model', 'electro/props_rubilnik_01_add1.m2033model', 'electro/rubilnik_4.m2033model', 'electro/rubilnik_4_add1.m2033model', 'fence/kalitka_01.m2033model', 'fence/kalitka_01_add1.m2033model', 'fireplace/barrel_01.m2033model', 'fireplace/candle_church01.m2033model', 'fireplace/candle_church02.m2033model', 'fireplace/fireplace_01.m2033model', 'germo/germo1.m2033model', 'germo/germo2.m2033model', 'germo/germo2_add1.m2033model', 'germo/germo3.m2033model', 'germo/germo3_add1.m2033model', 'germo/germo3_add2.m2033model', 'germo/germo3_add3.m2033model', 'germo/germo3_add4.m2033model', 'germo/germo3_add5.m2033model', 'germo/germo3_add6.m2033model', 'germo/germo3_add7.m2033model', 'germo/germo4.m2033model', 'germo/germo4_add1.m2033model', 'germo/germo5.m2033model', 'germo/germo5_add1.m2033model', 'germo/germo6.m2033model', 'germo/germo6_add1.m2033model', 'germo/germo6_add2.m2033model', 'germo/germo7.m2033model', 'germo/germo7_add1.m2033model', 'germo/germo7_add2.m2033model', 'germo/germo8.m2033model', 'germo/germo8_add1.m2033model', 'germo/germo8_add2.m2033model', 'germo/germo9.m2033model', 'germo/germo9_add1.m2033model', 'house/small_door.m2033model', 'items/baggage/rukzak.m2033model', 'items/cyan_light_stick.m2033model', 'items/filter.m2033model', 'items/green_light_stick.m2033model', 'lamp/kerosinka_02.m2033model', 'lamp/kerosinka_03.m2033model', 'lamp/kerosinka_05.m2033model', 'lamp/l04_lampion01.m2033model', 'lamp/l04_lampion02.m2033model', 'lamp/light_tripple1.m2033model', 'lamp/light_tripple1_add1.m2033model', 'lamp/svetilnik.m2033model', 'parom/ubl_ammo_01.m2033model', 'parom/ubl_ammo_02.m2033model', 'patron/ammo_01.m2033model', 'patron/mnogo_ammo_01.m2033model', 'patron/wpn_14_5x5_ammo.m2033model', 'patron/wpn_7_62_ammo.m2033model', 'props_library/wood_door.m2033model', 'station_props/lightbox.m2033model', 'structure/d6_door_02.m2033model', 'structure/d6_door_02_add1.m2033model', 'structure/gates_01.m2033model', 'structure/l13_reshetka_skin.m2033model', 'structure/l19_polis_door.m2033model', 'structure/l19_polis_door_add1.m2033model', 'structure/l19_polis_door_add2.m2033model', 'structure/l19_polis_door_add3.m2033model', 'structure/l19_polis_door_add4.m2033model', 'structure/mls_vorota_l06.m2033model', 'structure/mls_vorota_l06_add1.m2033model', 'structure/mls_vorota_l06_add2.m2033model', 'structure/reshetka_02.m2033model', 'structure/reshetka_02_add1.m2033model', 'structure/rubilnik_01.m2033model', 'structure/rubilnik_01_add1.m2033model', 'structure/rubilnik_02.m2033model', 'structure/rubilnik_02_add1.m2033model', 'structure/rubilnik_lift.m2033model', 'structure/rubilnik_lift_add1.m2033model', 'structure/shitovaya_01.m2033model', 'structure/shitovaya_01_add1.m2033model', 'structure/vorota_l03.m2033model', 'structure/vorota_l03_add1.m2033model', 'structure/vorota_l03_add2.m2033model', 'structure/vorota_l03_add3.m2033model', 'structure/vorota_resh_tower.m2033model', 'structure/vorota_resh_tower_add1.m2033model', 'structure/vorota_resh_tower_add2.m2033model', 'structure/zatvor_01.m2033model', 'structure/zatvor_01_add1.m2033model', 'structure/zatvor_01_add2.m2033model', 'structure/zatvor_01_add3.m2033model', 'structure/zatvor_01_add4.m2033model', 'structure/zatvor_01_add5.m2033model', 'tunnel_props/tonnels_props_lamp_germ_01.m2033model', 'wpn/predohranitel_bomb_sticky.m2033model', 'wpn/wpn_bomb_predohranitel.m2033model')

LIMIT = 90 * 1024 * 1024


def safe_name(name):
    p = PurePosixPath(name)
    return (not p.is_absolute() and not any(s in ('.', '..') for s in name.split('/'))
            and not any(c in name for c in ('\\', ':', '\x00')))


def collect(source, output, references=MODEL_REFERENCES, limit=LIMIT):
    source, output = Path(source), Path(output)
    if limit < 1024 or limit > LIMIT:
        raise ValueError('Invalid ZIP size limit')
    # Never overwrite previous results, even partially completed ones.
    if output.exists():
        raise FileExistsError('Output already exists; choose a new --output directory: ' + str(output))
    selected, missing = {}, []
    with zipfile.ZipFile(source) as archive:
        entries = archive.infolist()
        for ref in references:
            matches = [i for i in entries if not i.is_dir() and (i.filename == ref or i.filename.endswith('/' + ref))]
            if len(matches) > 1:
                raise ValueError('Ambiguous archive paths for ' + ref)
            if not matches:
                missing.append(ref)
                continue
            info = matches[0]
            if not safe_name(info.filename) or stat.S_ISLNK(info.external_attr >> 16):
                raise ValueError('Unsafe archive path: ' + info.filename)
            # STORE avoids unpredictable compression expansion. Reserve ZIP headers.
            cost = info.file_size + 2 * len(info.filename.encode('utf-8')) + 256
            if cost + 256 > limit:
                raise ValueError('A single model exceeds part limit: ' + info.filename)
            selected[ref] = (info, cost)
        output.mkdir(parents=True)
        records, parts = [], []
        writer, size, part = None, 256, None
        try:
            for ref, (info, cost) in selected.items():
                if writer is None or size + cost > limit:
                    if writer is not None:
                        writer.close()
                    part = output / ('metro_decor_models_%02d.zip' % (len(parts) + 1))
                    parts.append(part)
                    writer = zipfile.ZipFile(part, 'x', compression=zipfile.ZIP_STORED, allowZip64=False)
                    size = 256
                digest, count = hashlib.sha256(), 0
                with archive.open(info) as src, writer.open(info.filename, 'w') as dst:
                    for chunk in iter(lambda: src.read(1024 * 1024), b''):
                        count += len(chunk)
                        if count > info.file_size:
                            raise ValueError('Member size exceeded: ' + info.filename)
                        dst.write(chunk)
                        digest.update(chunk)
                if count != info.file_size:
                    raise ValueError('Incomplete member: ' + info.filename)
                records.append(dict(reference=ref, path=info.filename, bytes=count,
                                    sha256=digest.hexdigest(), part=part.name))
                size += cost
        finally:
            if writer is not None:
                writer.close()
    def sha(path):
        h = hashlib.sha256()
        with path.open('rb') as f:
            for chunk in iter(lambda: f.read(1024 * 1024), b''):
                h.update(chunk)
        return h.hexdigest()
    part_records = []
    for p in parts:
        if p.stat().st_size > limit:
            raise ValueError('Output exceeds upload limit: ' + str(p))
        with zipfile.ZipFile(p) as z:
            if z.testzip() is not None:
                raise ValueError('Output CRC failure: ' + str(p))
        part_records.append(dict(name=p.name, bytes=p.stat().st_size, sha256=sha(p)))
    report = dict(source=source.name, source_sha256=sha(source), matched=len(records),
                  expected=len(references), missing=missing, files=records, parts=part_records,
                  status='original functional-decor source models only; internal dependencies not yet audited; not a runtime port')
    (output / 'decor_model_inventory.json').write_text(json.dumps(report, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    return report


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('models_jar', help='Local Metro2033Models.jar, not a Drive link')
    parser.add_argument('--output', default='metro_decor_parts', help='New output directory')
    args = parser.parse_args()
    result = collect(args.models_jar, args.output)
    print('Collected %d/%d direct model references into %d ZIP(s).' % (result['matched'], result['expected'], len(result['parts'])))
    print('Output:', Path(args.output).resolve())
    print('No donor code executed. No source files changed. Texture dependencies are not included.')
    if result['missing']:
        print('Missing references listed in decor_model_inventory.json; do not treat this as a complete scene.')
        raise SystemExit(2)
