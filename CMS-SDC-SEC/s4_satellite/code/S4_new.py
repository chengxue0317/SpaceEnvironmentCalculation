import json
import os
import sys
import satellite_coordinate_calculation
import satellite
import datetime

def xingxia_data(xing_zuobiao, start, end):
    coordinate_key_list,coordinate_value_list = [], []
    coordinate_key = list(xing_zuobiao.keys())
    for h in range(len(coordinate_key)):
        time = coordinate_key[h][:4] + '-' + coordinate_key[h][4:6] + '-' + coordinate_key[h][6:8] + ' ' + \
               coordinate_key[h][8:10] + ':' + coordinate_key[h][10:12] + ':' + coordinate_key[h][12:14]
        if datetime.datetime.strptime(start.replace('"',''), "%Y-%m-%d %H:%M:%S") <= datetime.datetime.strptime(str(time).replace('"',''),"%Y-%m-%d %H:%M:%S") and \
                datetime.datetime.strptime(str(time).replace('"',''), "%Y-%m-%d %H:%M:%S") <= datetime.datetime.strptime(end.replace('"',''), "%Y-%m-%d %H:%M:%S"):
            coordinate_key_list.append(coordinate_key[h])
    for j in range(len(coordinate_key_list)):
        coordinate_value_list.append(xing_zuobiao[coordinate_key_list[j]])
    return coordinate_value_list

def get_param(endTime,forecastPeriod,interval,p1Channel,p2Channel,prn,statTime,system,time,paramsSystem):

    key = ['time','system','PRN','interval','Forecast_Period','params_system','P1_channel','P2_channel','stat_time','end_time']
    value = [time.replace('"',''),system,prn,interval,forecastPeriod,paramsSystem,p1Channel,p2Channel, statTime.replace('"',''),endTime.replace('"','')]
    start = statTime.replace('"','')
    end = endTime.replace('"','')
    return key, value, start, end

def get_file(file_path, value):

    file_xingli,file_guance,ehm_list = [], [], []
    for i in os.listdir(file_path):
        if i.split('.')[-1] == 'rnx':
            file_xingli.append(i)
        else:
            file_guance.append(i)
    eh_country, eh_source, eh_intertime, eh_type, eh_style = [], [], [], [], []
    for j in range(len(file_xingli)):
        eh_country.append(file_xingli[j].split('_')[0][-3:])
        eh_source.append(file_xingli[j].split('_')[1])
        eh_intertime.append(file_xingli[j].split('_')[3])
        eh_type.append(file_xingli[j].split('_')[4].strip('.rnx'))
        eh_style.append(file_xingli[j].split('_')[-1].split('.')[-1])
    obs_station_name,obs_MR,obs_country,obs_source,obs_interval_mimute,obs_interval_second,obs_type = [], [], [], [], [], [], []
    for k in range(len(file_guance)):
        obs_station_name.append(file_guance[k].split('_')[0][:4])
        obs_MR.append(file_guance[k].split('_')[0][4:6])
        obs_country.append(file_guance[k].split('_')[0][6:])
        obs_source.append(file_guance[k].split('_')[1])
        obs_interval_mimute.append(file_guance[k].split('_')[3])
        obs_interval_second.append(file_guance[k].split('_')[4])
        obs_type.append(file_guance[k].split('_')[5].split('.23o')[0])

    for h in range(len(obs_station_name)):

        dict_key = ['time', 'system', 'PRN', 'interval', 'Forecast Period',  'station_name', 'MR', 'obs_country', 'obs_source', 'obs_interval_mimute', 'obs_interval_second',
                    'obs_type', 'ephem_country', 'ephem_source', 'ephem_intertime', 'ephem_type', 'ephem_style']
        dict_value = [value[0], value[1], value[2], value[3], value[4], obs_station_name[h], obs_MR[h], obs_country[h], obs_source[h], obs_interval_mimute[h], obs_interval_second[h],
                      obs_type[h], eh_country[h], eh_source[h], eh_intertime[h], eh_type[h], eh_style[h]]
        all_params = dict(zip(dict_key, dict_value))
        return all_params

def main(file_path, endTime,forecastPeriod,interval,p1Channel,p2Channel,prn,statTime,system,time,paramsSystem):

    key, value, start, end = get_param(endTime,forecastPeriod,interval,p1Channel,p2Channel,prn,statTime,system,time,paramsSystem)
    all_params = get_file(file_path, value)
    strength_value_S1, strength_value_S2, file_time = satellite.main(file_path, value[5], value[2], value[6], value[7],value[8],value[9])
    info = satellite_coordinate_calculation.ephemeris_data_analysis(all_params, file_path).data_analysis()
    xing_zuobiao = info[1]
    dimian_zuobiao = info[3]
    coordinate_value_list = xingxia_data(xing_zuobiao, file_time[0], file_time[-1])
    all_dict = {
        'L1_S4': strength_value_S1,
        'L2_S4': strength_value_S2,
        'S4_time': file_time,
        'ground_coordinate_value': dimian_zuobiao,
        'satellite_coordinate_value': coordinate_value_list}
    print(all_dict)

if __name__ == '__main__':

    file_path = sys.argv[1]         #'..//data//'
    endTime = sys.argv[2]           #'2023-01-01 00:40:00'
    forecastPeriod = sys.argv[3]    #'10800'
    interval = sys.argv[4]          #'600'
    p1Channel = sys.argv[5]         #'C1C'
    p2Channel = sys.argv[6]         #'C1W'
    prn = sys.argv[7]               #'01'
    statTime = sys.argv[8]          #'2023-01-01 00:10:00'
    system = sys.argv[9]            #'GPS'
    time = sys.argv[10]             #'2023-01-01 8:00:00'
    paramsSystem = sys.argv[11]     #'G'

    main(file_path, endTime,forecastPeriod,interval,p1Channel,p2Channel,prn,statTime,system,time,paramsSystem)

