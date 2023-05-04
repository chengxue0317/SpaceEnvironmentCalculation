"""
这个程序是用来计算给定的时间段内，卫星穿过南大西洋异常区次数以及每次穿越的起止时间和用时
"""
import os
import sys
import json
import argparse
import dmPython
from pathlib import Path
import datetime
import matplotlib.path as mplpath
import hull

sys.path.append(str(Path(__file__).resolve().parents[2]) + "/pythonPlot")
sys.path.append(str(Path(__file__).resolve().parents[1]))
sys.path.append(str(Path(__file__).resolve().parents[1]) + '/GlobalRadiationDistribution')

import global_radiation as gr
from read_flux_data import read_flux_data
import global_radiation_plot as grp

import pandas as pd


class MyEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, datetime.datetime):
            print("MyEncoder-datetime.datetime")
            return obj.strftime("%Y-%m-%d %H:%M:%S")
        if isinstance(obj, bytes):
            return str(obj, encoding='utf-8')
        if isinstance(obj, int):
            return int(obj)
        elif isinstance(obj, float):
            return float(obj)
        # elif isinstance(obj, array):
        #    return obj.tolist()
        else:
            return super(MyEncoder, self).default(obj)


def get_saa_flux(time_start, altitude, whatf, whichm):
    san = gr.solar_activity(time_start)
    return gr.main(time_start, altitude, whatf, whichm, san)  # return filedir, filenum


def get_lrtb_points(flux):
    """
    获取南大西洋异常区最东、最西、最南、最北的四个点
    """

    for lat in range(flux.shape[0]):
        if any(flux.iloc[lat, :] >= 1):
            lat_bottom = flux.index[lat]
            break

    for lat in range(flux.shape[0] - 1, 0, -1):
        if any(flux.iloc[lat, :] >= 1):
            lat_top = flux.index[lat]
            break

    if any(flux.iloc[:, 0] >= 1):
        for lon in range(flux.shape[1]):
            if all(flux.iloc[:, lon] < 1):
                lon_right = flux.columns[lon - 1][1]
                break
    else:
        for lon in range(0, flux.shape[1], 1):
            if any(flux.iloc[:, lon] >= 1):
                lon_right = flux.columns[lon][1]
                break

    if any(flux.iloc[:, flux.shape[1] - 1] >= 1):
        for lon in range(flux.shape[1] - 1, 0, -1):
            if all(flux.iloc[:, lon] < 1):
                lon_left = flux.columns[lon + 1][1]
                break
    else:
        for lon in range(flux.shape[1] - 1, 0, -1):
            if any(flux.iloc[:, lon] >= 1):
                lon_left = flux.columns[lon][1]
                break
    return lon_left, lon_right, lat_top, lat_bottom


def get_sat_range(sat_id, start_time, end_time, left_lon, right_lon, top_lat, bottom_lat):
    """
    识别南大西洋异常区范围
    """
    def Connect_SQL(iniPath):
        from configparser import ConfigParser
        cfg = ConfigParser()
        cfg.read(iniPath)
        sql_cfg = dict(cfg.items("dmsql"))
        conn = dmPython.connect(
            user=sql_cfg['user'],
            password=sql_cfg['password'],
            server=sql_cfg['server'],
            port=sql_cfg['port'])
        cursor = conn.cursor()
        return cursor,conn

    try:
        iniPath = os.path.dirname(os.path.abspath(__file__)).split('/CMS-SDC-SEC')[0]+'/DLXJS_DB.ini'
        cursor,conn = Connect_SQL(iniPath)
    except (dmPython.Error, Exception) as err:
        print(err)

    if left_lon > right_lon:
        sql = "SELECT LAT,LON,ALT,TIME FROM SEC_SATELLITE_LLA"
        sql += " WHERE SAT_ID = '" + sat_id
        sql += "' AND TIME >= '" + start_time + ".000'"
        sql += " AND TIME <= '" + end_time + ".000'"
        sql += " AND LAT >= " + str(bottom_lat) + " AND LAT <= " + str(top_lat)
        sql += " AND (LON >= " + str(left_lon) + " OR LON <= " + str(right_lon) + ")"
    else:
        sql = "SELECT LAT,LON,ALT,TIME FROM SEC_SATELLITE_LLA"
        sql += " WHERE SAT_ID = '" + sat_id
        sql += "' AND TIME >= '" + start_time + ".000'"
        sql += " AND TIME <= '" + end_time + ".000'"
        sql += " AND LAT >= " + str(bottom_lat) + " AND LAT <= " + str(top_lat)
        sql += " AND LON >= " + str(left_lon) + " AND LON <= " + str(right_lon)

    cursor.execute(sql)
    res = cursor.fetchall()

    list_out = []
    for i in res:
        list_out.append(list(i))
    sat_info = pd.DataFrame(list_out, columns=['lat', 'lon', 'alt', 'date'])
    sat_info['date'] = sat_info['date'].astype('str')
    sat_info['time'] = str(start_time[0:19])

    if left_lon > right_lon:
        sat_info.loc[sat_info["lon"] > left_lon, "lon"] = sat_info['lon'] - 360

    return sat_info


def get_saa_points(flux):
    saa_points = []
    left, right, temp, temp = get_lrtb_points(flux)
    if left > right:
        for lat in range(flux.shape[0]):
            for lon in range(flux.shape[1]):
                if flux.iloc[lat, lon] >= 1:
                    if flux.columns[lon][1] >= left:
                        saa_points.append(tuple((flux.index[lat], flux.columns[lon][1] - 360)))
                    else:
                        saa_points.append(tuple((flux.index[lat], flux.columns[lon][1])))
    else:
        for lat in range(flux.shape[0]):
            for lon in range(flux.shape[1]):
                if flux.iloc[lat, lon] >= 1:
                    saa_points.append(tuple((flux.index[lat], flux.columns[lon][1])))

    return saa_points


def get_saa_edges(saa_points=None):
    if saa_points is None:
        saa_points = get_saa_points(flux)
    saa_convex = hull.convex(saa_points)
    return saa_convex


def judeg_io(saa_convex, sat_trace, k=1):
    path = mplpath.Path(saa_convex)
    inside = path.contains_points(sat_trace)

    status = pd.DataFrame(columns=['status', 'time'])
    for i in range(len(inside)):
        if inside[i - 1] == False and inside[i] == True:
            if sum(inside[i - k:i]) == 0 and sum(inside[i:i + k]) == k:
                df = pd.DataFrame([{'status': 'entry', 'time': Satellite_restricted_trace['date'][i]}])
                status = pd.concat([status, df], ignore_index=True)
        if inside[i - 1] == True and inside[i] == False:
            if sum(inside[i - k:i]) == k and sum(inside[i:i + k]) == 0:
                df = pd.DataFrame([{'status': 'left', 'time': Satellite_restricted_trace['date'][i]}])
                status = pd.concat([status, df], ignore_index=True)

    if status.iloc[0, 0] == 'left':
        first_item = pd.DataFrame([{'status': 'entry', 'time': None}])
        status = pd.concat([first_item, status], ignore_index=True)
    if status.iloc[-1, 0] == 'entry':
        last_item = pd.DataFrame([{'status': 'left', 'time': None}])
        status = pd.concat([status, last_item], ignore_index=True)

    io = pd.DataFrame(columns=['entry', 'left', 'duration'])

    io['entry'] = list(status.iloc[::2, 1])
    io['left'] = list(status.iloc[1::2, 1])
    io['duration'] = list(pd.to_datetime(io['left']) - pd.to_datetime(io['entry']))
    io = io.fillna(pd.NaT).replace([pd.NaT], [None])

    io['duration'] = list(map(str, io['duration']))
    io['entry'] = list(map(str, status.iloc[::2, 1]))
    io['left'] = list(map(str, status.iloc[1::2, 1]))

    io_json = []
    for i in range(io.shape[0]):
        io_json.append({'entry': io.loc[i, 'entry'], 'left': io.loc[i, 'left'], 'duration': io.loc[i, 'duration']})

    return io_json


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    # 使用 `-` 来指明短选项
    # 使用 `--` 来指明可选项
    parser.add_argument("-s", "--satname", type=str, help="satellite name", default="XWINC011")
    parser.add_argument("-st", "--start_time", type=str, help="date and time, ex: 2022-11-07 04:00:00",
                        default="2022-11-07 05:12:00")
    parser.add_argument("-et", "--end_time", type=str, help="date and time, ex: 2022-11-07 16:00:00",
                        default="2022-11-08 02:45:00")
    parser.add_argument("-a", "--altitude", type=int, help="satellite altitude, unit: km", default=1151)
    parser.add_argument("-f", "--whatf", type=str, help="differential - 1, range - 2, above - 3", default='above')
    parser.add_argument("-m", "--whichm", type=str, help="ae8 - 1, ap8 - 2", default='ap8')
    parser.add_argument("-c", "--channel", type=str, help="ae8: 5 channels P1-P5; ap8: 6 channels P1-P6", default='P3')
    args = parser.parse_args()
    args.start_time = args.start_time.replace('"', '')
    args.end_time = args.end_time.replace('"', '')

    def Connect_SQL(iniPath):
        from configparser import ConfigParser
        cfg = ConfigParser()
        cfg.read(iniPath)
        sql_cfg = dict(cfg.items("dmsql"))
        conn = dmPython.connect(
            user=sql_cfg['user'],
            password=sql_cfg['password'],
            server=sql_cfg['server'],
            port=sql_cfg['port'])
        cursor = conn.cursor()
        return cursor,conn

    try:
        iniPath = os.path.dirname(os.path.abspath(__file__)).split('/CMS-SDC-SEC')[0]+'/DLXJS_DB.ini'
        cursor,conn = Connect_SQL(iniPath)
    except (dmPython.Error, Exception) as err:
        print(err)

    sql = "select count(*) from SEC_SATELLITE_LLA where SAT_ID = '" + args.satname + "'"
    cursor.execute(sql)
    res = cursor.fetchall()
    if res[0][0] == 0:
        print('Null')
        exit()

    filedir, filenum = get_saa_flux(args.start_time, args.altitude, args.whatf, args.whichm)
    flux_file_path = filedir + '/' + filenum

    # flux_file_path = r'/home/wangrry/work/SEC/AP8AE8/84037200/flux.txt'
    flux = read_flux_data(args.whichm, args.channel, flux_file_path + '/flux.txt')
    grp.draw(flux, flux_file_path)

    lon_left, lon_right, lat_top, lat_bottom = get_lrtb_points(flux)
    Satellite_restricted_trace = get_sat_range(args.satname, args.start_time, args.end_time, lon_left, lon_right, lat_top, lat_bottom)
    saa_points = get_saa_points(flux)
    saa_edges = get_saa_edges(saa_points)
    io = judeg_io(saa_edges, Satellite_restricted_trace[['lat', 'lon']])

    info_json = {'plot_path': flux_file_path, 'data': io}
    info_json = json.dumps(info_json, indent=4)
    print(info_json)

    os.system('rm -rf ' + filedir + '/' + filenum + '/flux.txt')
