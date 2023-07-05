import os
import math
import time
import datetime
import sys
import numpy as np
from mpl_toolkits.basemap import Basemap
import satellite_coordinate_calculation
from pykrige.ok import OrdinaryKriging
import matplotlib.pyplot as plt
import matplotlib.ticker as ticker
import matplotlib as mpl
import warnings
warnings.filterwarnings("ignore")

#制图
def get_ficture(ROTI_value_G,stars_coordinate_list_new_G,ROTI_value_C,stars_coordinate_list_new_C,total_time,figure_save_path,ss):
    if not os.path.exists(figure_save_path):
        os.makedirs(figure_save_path)
    new_data, new_lonlat, lon_new, lat_new, roti_new = [], [], [], [], []
    for t in range(len(stars_coordinate_list_new_G)):
        new_lonlat.append(stars_coordinate_list_new_G[t] +stars_coordinate_list_new_C[t])
        new_data.append(ROTI_value_G[t] + ROTI_value_C[t])
    plt.figure(figsize=(10, 6))
    # 添加颜色条
    norm = mpl.colors.Normalize(vmin=0, vmax=1)
    plt.ylabel('Latitude/(deg)', labelpad=40, fontsize=12.5)
    plt.xlabel('Longitude/(deg)', labelpad=15, fontsize=12.5)
    sm = plt.cm.ScalarMappable(norm=norm, cmap='jet')
    cb = plt.colorbar(sm, fraction=0.023, pad=0.08, cmap='jet',)
    tick_locator = ticker.MaxNLocator(nbins=4)
    cb.locator = tick_locator
    cb.update_ticks()
    cb.ax.tick_params(labelsize=12.5)
    # 设置颜色条的title
    cb.ax.set_title('TECU($10^{16}$el/min)', fontsize=12.5)
    for g in range(len(new_data)):
        lon_new, lat_new, roti_new = [],[],[]
        for h in range(len(new_data[g])):
            lon_new.append(new_lonlat[g][h][0])
            lat_new.append(new_lonlat[g][h][1])
            roti_new.append(new_data[g][h])

        # 创建地图
        map = Basemap()
        map.drawcoastlines()
        x, y = map(lon_new, lat_new)
        map.scatter(x, y, c=roti_new, cmap='jet', marker='o')
        map = Basemap(llcrnrlon=-180, llcrnrlat=-90, urcrnrlon=180, urcrnrlat=90)
        map.drawcountries(linewidth=0.8, color='k')
        map.drawcoastlines(linewidth=0.8, color='k')
        parallels = np.linspace(-90, 90, 6)
        map.drawparallels(parallels, labels=[True, False, False, False])
        meridians = np.linspace(-180, 180, 6)
        map.drawmeridians(meridians, labels=[False, False, False, True])

        degree = 15
        gridx = np.arange(-180, 181, degree).astype(float)
        gridy = np.arange(90, -91, -degree).astype(float)
        gridx_new = np.tile(gridx,13).reshape(13, 25)
        gridy_new = (gridy.repeat(25)).reshape(13, 25)
        ok3d = OrdinaryKriging(lon_new, lat_new, roti_new, variogram_model="linear")  # 模型
        k3d1, ss3d = ok3d.execute("grid", gridx, gridy)  # k3d1是结果，给出了每个网格点处对应的值
        zz = np.round(k3d1, 2)

        plt.contourf(gridx_new,gridy_new,zz,marker='s',cmap='jet',levels=500)
        mkfile_time = total_time[g].replace('-', '').replace(':', '').replace(' ', '')
        plt.title(label="ROTI_Global" + '  ' + mkfile_time)
        plt.savefig(os.path.join(figure_save_path, mkfile_time), bbox_inches='tight', pad_inches=0.0)
        print("结果保存成功！！！！！")
        ee = time.time()
    print("代码运行时间：", int(ee) - int(ss))
    exit() #结束循环

#获取ROTI数据
def get_ROTI(STEC_value):
    ROT_value_list, ROTI_value_list = [], []
    STEC_len_prn = list(range(0, len(STEC_value[0])))   #随机生成列表
    for h in STEC_len_prn:
        STEC_data_column = [row[int(STEC_len_prn[h])] for row in STEC_value]   #去除二维列表中指定列数
        ROT_data_average = sum(STEC_data_column)/len(STEC_data_column)
        for d in range(len(STEC_data_column)):
            if d != 0 :
                ROTI_value = math.sqrt(((STEC_data_column[d]-ROT_data_average)**2 + (STEC_data_column[d-1] - ROT_data_average)**2)/2)
            else:
                ROTI_value = math.sqrt((STEC_data_column[d]-ROT_data_average)**2)
            ROTI_value_list.append(ROTI_value)
    ROTI_value_list_cut = cut(ROTI_value_list, len(STEC_value))
    return ROTI_value_list_cut

#获取垂向TEC数据
def get_V_TEC(all_STEC_value,degree,stars_coordinate_list,Zenith_distance_list,stat_time, end_time,all_time):
    V_TEC_value, S_TEC,time1 = [], [], []
    time_new,Zenith_distance_list_new,stars_coordinate_list_new ,all_stars_coordinate_list_new,all_Zenith_distance_list_new = [],[],[],[],[]
    for h in range(len(Zenith_distance_list)):
        for u in range(len(Zenith_distance_list[h])):
            Zenith_distanc_time = list(Zenith_distance_list[h].keys())[u][:4]+'-'+list(Zenith_distance_list[h].keys())[u][4:6]+'-'+list(Zenith_distance_list[h].keys())[u][6:8]+' '\
                                + list(Zenith_distance_list[h].keys())[u][8:10]+':'+list(Zenith_distance_list[h].keys())[u][10:12]+':'+list(Zenith_distance_list[h].keys())[u][12:]
            if datetime.datetime.strptime(stat_time.replace('"', ''), "%Y-%m-%d %H:%M:%S") <= datetime.datetime.strptime(str(Zenith_distanc_time).replace('"', ''), "%Y-%m-%d %H:%M:%S") and \
                datetime.datetime.strptime(str(Zenith_distanc_time).replace('"', ''),"%Y-%m-%d %H:%M:%S") <= datetime.datetime.strptime(end_time.replace('"', ''),"%Y-%m-%d %H:%M:%S"):
                if datetime.datetime.strptime('2023-01-02 00:00:00', "%Y-%m-%d %H:%M:%S") <= datetime.datetime.strptime(str(Zenith_distanc_time).replace('"', ''), "%Y-%m-%d %H:%M:%S") and \
                datetime.datetime.strptime(str(Zenith_distanc_time).replace('"', ''),"%Y-%m-%d %H:%M:%S") <= datetime.datetime.strptime('2023-01-02 00:50:00',"%Y-%m-%d %H:%M:%S"):
                    time_new.append(Zenith_distanc_time)
                    #获取天顶距数据，并将该坐标存放到列表中
                    Zenith_distance_list_new.append(list(Zenith_distance_list[h].values())[u])
                    #获取星下点坐标，并将该坐标存放到列表中
                    stars_coordinate_list_new.append(list(stars_coordinate_list[h].values())[u][:2])
    total_time = list(dict.fromkeys(time_new))
    for s in range(len(all_STEC_value)):
            if datetime.datetime.strptime(stat_time.replace('"', ''), "%Y-%m-%d %H:%M:%S") <= datetime.datetime.strptime(str(all_time[s]).replace('"', ''), "%Y-%m-%d %H:%M:%S") and \
                datetime.datetime.strptime(str(all_time[s]).replace('"', ''),"%Y-%m-%d %H:%M:%S") <= datetime.datetime.strptime(end_time.replace('"', ''),"%Y-%m-%d %H:%M:%S"):
                S_TEC.append(all_STEC_value[s])   #获取符合时间范围内的斜向TEC
    ROTI_value_list_cut = get_ROTI(S_TEC)
    len_list_degree = int(len(stars_coordinate_list_new) / degree)
    if len_list_degree == 7:
        list1_st, list2_st, list3_st, list4_st, list5_st, list6_st, list7_st,list1_ze, list2_ze, list3_ze, list4_ze, list5_ze, list6_ze, list7_ze= [], [], [], [], [], [], [], [], [], [], [], [], [], []
        for d in range(len(stars_coordinate_list_new)):
            if d % int(len(stars_coordinate_list_new) / degree) == 0:
                list1_st.append(stars_coordinate_list_new[d])
                list2_st.append(stars_coordinate_list_new[d + 1])
                list3_st.append(stars_coordinate_list_new[d + 2])
                list4_st.append(stars_coordinate_list_new[d + 3])
                list5_st.append(stars_coordinate_list_new[d + 4])
                list6_st.append(stars_coordinate_list_new[d + 5])
                list7_st.append(stars_coordinate_list_new[d + 6])
                list1_ze.append(Zenith_distance_list_new[d])
                list2_ze.append(Zenith_distance_list_new[d + 1])
                list3_ze.append(Zenith_distance_list_new[d + 2])
                list4_ze.append(Zenith_distance_list_new[d + 3])
                list5_ze.append(Zenith_distance_list_new[d + 4])
                list6_ze.append(Zenith_distance_list_new[d + 5])
                list7_ze.append(Zenith_distance_list_new[d + 6])
                d = d + 6
        all_stars_coordinate_list_new = list1_st + list2_st + list3_st + list4_st + list5_st + list6_st
        return ROTI_value_list_cut, all_stars_coordinate_list_new, total_time

    if len_list_degree == 6:
        list1_st, list2_st, list3_st, list4_st,list5_st, list6_st, list1_ze, list2_ze, list3_ze, list4_ze, list5_ze, list6_ze= [], [], [], [], [], [], [], [], [], [], [], []
        for d in range(len(stars_coordinate_list_new)):
            if d % int(len(stars_coordinate_list_new) / degree) == 0:
                list1_st.append(stars_coordinate_list_new[d])
                list2_st.append(stars_coordinate_list_new[d + 1])
                list3_st.append(stars_coordinate_list_new[d + 2])
                list4_st.append(stars_coordinate_list_new[d + 3])
                list5_st.append(stars_coordinate_list_new[d + 4])
                list6_st.append(stars_coordinate_list_new[d + 5])
                list1_ze.append(Zenith_distance_list_new[d])
                list2_ze.append(Zenith_distance_list_new[d + 1])
                list3_ze.append(Zenith_distance_list_new[d + 2])
                list4_ze.append(Zenith_distance_list_new[d + 3])
                list5_ze.append(Zenith_distance_list_new[d + 4])
                list6_ze.append(Zenith_distance_list_new[d + 5])
                d = d + 5
        all_stars_coordinate_list_new = list1_st + list2_st + list3_st + list4_st + list5_st + list6_st
        return ROTI_value_list_cut, all_stars_coordinate_list_new, total_time

    if len_list_degree == 5:
        list1_st, list2_st, list3_st, list4_st,list5_st,  list1_ze, list2_ze, list3_ze, list4_ze, list5_ze = [], [], [], [], [], [], [], [], [], []
        for d in range(len(stars_coordinate_list_new)):
            if d % int(len(stars_coordinate_list_new) / degree) == 0:
                list1_st.append(stars_coordinate_list_new[d])
                list2_st.append(stars_coordinate_list_new[d + 1])
                list3_st.append(stars_coordinate_list_new[d + 2])
                list2_st.append(stars_coordinate_list_new[d + 3])
                list3_st.append(stars_coordinate_list_new[d + 4])
                list1_ze.append(Zenith_distance_list_new[d])
                list2_ze.append(Zenith_distance_list_new[d + 1])
                list3_ze.append(Zenith_distance_list_new[d + 2])
                list4_ze.append(Zenith_distance_list_new[d + 3])
                list5_ze.append(Zenith_distance_list_new[d + 4])
                d = d + 4
        all_stars_coordinate_list_new = list1_st + list2_st + list3_st +list4_st +list5_st
        return ROTI_value_list_cut, all_stars_coordinate_list_new, total_time

    if len_list_degree == 4:
        list1_st, list2_st, list3_st, list4_st, list1_ze, list2_ze, list3_ze, list4_ze = [], [], [], [], [], [], [], []
        for d in range(len(stars_coordinate_list_new)):
            if d % int(len(stars_coordinate_list_new) / degree) == 0:
                list1_st.append(stars_coordinate_list_new[d])
                list2_st.append(stars_coordinate_list_new[d + 1])
                list3_st.append(stars_coordinate_list_new[d + 2])
                list4_st.append(stars_coordinate_list_new[d + 3])
                list1_ze.append(Zenith_distance_list_new[d])
                list2_ze.append(Zenith_distance_list_new[d + 1])
                list3_ze.append(Zenith_distance_list_new[d + 2])
                list4_ze.append(Zenith_distance_list_new[d + 3])
                d = d + 3
        all_stars_coordinate_list_new = list1_st + list2_st + list3_st + list4_st
        return ROTI_value_list_cut, all_stars_coordinate_list_new, total_time

    if len_list_degree == 3:
        list1_st, list2_st, list3_st,list1_ze, list2_ze, list3_ze = [], [], [], [], [], []
        for d in range(len(stars_coordinate_list_new)):
            if d % int(len(stars_coordinate_list_new) / degree) == 0:
                list1_st.append(stars_coordinate_list_new[d])
                list2_st.append(stars_coordinate_list_new[d + 1])
                list3_st.append(stars_coordinate_list_new[d + 2])
                list1_ze.append(Zenith_distance_list_new[d])
                list2_ze.append(Zenith_distance_list_new[d + 1])
                list3_ze.append(Zenith_distance_list_new[d + 2])
                d = d + 2
        all_stars_coordinate_list_new = list1_st + list2_st + list3_st
        return ROTI_value_list_cut, all_stars_coordinate_list_new, total_time

    if len_list_degree == 2:
        list1_st, list2_st, list1_ze, list2_ze= [], [], [], []
        for d in range(len(stars_coordinate_list_new)):
            if d % int(len(stars_coordinate_list_new) / degree) == 0:
                list1_st.append(stars_coordinate_list_new[d])
                list2_st.append(stars_coordinate_list_new[d + 1])
                list1_ze.append(Zenith_distance_list_new[d])
                list2_ze.append(Zenith_distance_list_new[d + 1])
                d = d + 1
        all_stars_coordinate_list_new = list1_st + list2_st
        return ROTI_value_list_cut, all_stars_coordinate_list_new, total_time

    if len_list_degree == 1:
        list1_st, list1_ze = [], []
        for d in range(len(stars_coordinate_list_new)):
            if d % int(len(stars_coordinate_list_new) / degree) == 0:
                list1_st.append(stars_coordinate_list_new[d])
                list1_ze.append(Zenith_distance_list_new[d])
                d = d
        all_stars_coordinate_list_new = list1_st
        return ROTI_value_list_cut, all_stars_coordinate_list_new, total_time

#求和获取卫星每小时的斜向TEC数值
def sum_data(data, len_prn):
    rot_list, sum_STEC_min_list,sum_STEC_hours_list, all_sum_STEC_hours_list,ROTI_list = [], [], [], [], []
    list_len_prn = list(range(0, len_prn))
    for h in list_len_prn:
        data_column = [row[int(list_len_prn[h])] for row in data]
        data_column_cut = cut(data_column, int(60))
        for l in range(len(data_column_cut)):
            sum_STEC_min_list.append(sum(data_column_cut[l]))  #获取每秒的斜向STEC数值
            if len(sum_STEC_min_list) == int(10):
                sum_STEC_hours_list.append(sum(sum_STEC_min_list))    #获取10分钟的的斜向STEC数值
                sum_STEC_min_list = []
    sum_STEC_hours_list_cut = cut(sum_STEC_hours_list,len_prn)
    return sum_STEC_hours_list_cut

# 切分列表
def cut(obj, sec):
    return [obj[i:i + sec] for i in range(0, len(obj), sec)]

#利用传入的数据，判断卫星名称简写是否为C,如果是则获得卫星为C的TEC数据值
def get_TEC_C(file_data_list):
    C_tec_value = []
    fre_value_C= ['1268.52', '1561.098']  # 频率数据
    for k in range(len(file_data_list)):
        for w in range(len(file_data_list[k])):
            v_value_p1, v_value_p2 = [], []
            for r in range(len(file_data_list[k][w])):
                if r == 4:
                    v_value_p1.append(file_data_list[k][w][r])
                elif r == 2:
                    v_value_p2.append(file_data_list[k][w][r])
            if v_value_p1[0] == v_value_p2[0]:
                TEC = 0
            else:
                TEC = ((float(v_value_p2[0]) - float(v_value_p1[0])) * ((float(fre_value_C[0]) ** 2) * (float(fre_value_C[1]) ** 2)))/ ((float(fre_value_C[0]) ** 2 - float(fre_value_C[1]) ** 2) * 40.28)
            C_tec_value.append(TEC)
    C_tec_value_new = cut(C_tec_value, int(16))
    C_STEC_hours_list = sum_data(C_tec_value_new,int(16))
    all_C_prn_list = ['01', '02', '03', '04', '05', '09', '13', '16', '19', '21', '22', '38', '39', '45', '59', '60']
    return C_STEC_hours_list, all_C_prn_list

#利用传入的数据，判断卫星名称简写,是否为G,如果是则获得卫星为G的TEC数据值 ,采用伪距通道分别为'C1C'、'C2W'
def get_TEC_G(file_data_list):
    G_tec_value = []
    fre_value_G = ['1575.42', '1227.60']  # 频率数据
    for k in range(len(file_data_list)):
        for w in range(len(file_data_list[k])):
            v_value_p1, v_value_p2 = [],[]
            for r in range(len(file_data_list[k][w])):
                if r == 1:
                    v_value_p1.append(file_data_list[k][w][r])
                elif r == 5:
                    v_value_p2.append(file_data_list[k][w][r])
            if v_value_p1[0] == v_value_p2[0]:
                TEC = 0
            else:
                TEC = ((float(v_value_p2[0]) - float(v_value_p1[0])) * ((float(fre_value_G[0]) ** 2) * (float(fre_value_G[1]) ** 2)))/ ((float(fre_value_G[0]) ** 2 - float(fre_value_G[1]) ** 2) * 40.28)
            G_tec_value.append(TEC)
    G_tec_value_new = cut(G_tec_value, int(7))
    G_STEC_hours_list = sum_data(G_tec_value_new,int(7))
    all_G_prn_list = ['10', '12', '15', '18', '23', '24', '32']
    return G_STEC_hours_list, all_G_prn_list

#获取数据
def get_data(file_data_list,params,filepath, stat_time, end_time,all_time):
    G_data ,C_data, stars_coordinate_list_G, Zenith_distance_list_G, stars_coordinate_list_C, Zenith_distance_list_C = [], [], [], [], [], []
    for i in range(len(file_data_list)):
        d = file_data_list[i].replace('              ','  0 ').replace('    ',' 0  ').split()
        if d[0][0] == str('G') and d[0][1:3] != str('02') and d[0][0] == str('G') and d[0][1:3] != str('22') \
                and d[0][0] == str('G') and d[0][1:3] != str('25') and d[0][0] == str('G') and d[0][1:3] != str('31'):
            G_data.append(d)
        elif  d[0][0] == str('C') and d[0][1:] != str('06') and d[0][0] == str('C') and d[0][1:] != str('11') and d[0][1:] != str('12') and d[0][1:] != str('26') and d[0][1:] != str('34') and d[0][0] == str('C') and d[0][1:] != str('36')\
            and d[0][1:] != str('44'):
            C_data.append(d)
    G_data_list = cut(G_data,int(7))    #按照一定的长度切分列表
    C_data_list = cut(C_data,int(16))
    G_STEC_hours_list, all_G_prn_list = get_TEC_G(G_data_list)    #获取GPS卫星所有的卫星编号的斜向TEC数据(GPS)
    C_STEC_hours_list, all_C_prn_list = get_TEC_C(C_data_list)    #获取BDS卫星所有的卫星编号的斜向TEC数据(北斗：BDS)
    for k in range(len(all_G_prn_list)):
        time_parms = {'time': '2023-01-02 8:00:00', 'system': 'GPS', 'PRN': all_G_prn_list[k], 'interval': '600', 'Forecast Period': '7200'}
        new_params = {**time_parms,**params}   #将2个列表组合起来
        info = satellite_coordinate_calculation.ephemeris_data_analysis(new_params, filepath).data_analysis()
        # #获取G卫星的星下点坐标列表
        stars_coordinate_list_G.append(info[1])
        #获取G卫星的天顶距坐标列表
        Zenith_distance_list_G.append(info[6])
    for u in range(len(all_C_prn_list)):
        time_parms = {'time': '2023-01-02 8:00:00', 'system': 'BDS', 'PRN': all_C_prn_list[u], 'interval': '600','Forecast Period': '7200'}
        new_params = {**time_parms, **params}  # 将2个列表组合起来
        info = satellite_coordinate_calculation.ephemeris_data_analysis(new_params, filepath).data_analysis()
        # #获取C卫星的星下点坐标列表
        stars_coordinate_list_C.append(info[1])
        # 获取C卫星的天顶距坐标列表
        Zenith_distance_list_C.append(info[6])
    ROTI_value_G,stars_coordinate_list_new_G,total_time = get_V_TEC(G_STEC_hours_list, int(7), stars_coordinate_list_G,Zenith_distance_list_G,stat_time, end_time,all_time)
    ROTI_value_C,stars_coordinate_list_new_C,total_time = get_V_TEC(C_STEC_hours_list, int(16), stars_coordinate_list_C,Zenith_distance_list_C,stat_time, end_time,all_time)
    G_ROTI_value = list(map(list, zip(*ROTI_value_G)))
    C_ROTI_value = list(map(list, zip(*ROTI_value_C)))
    G_stars_coordinate_list_new = cut(stars_coordinate_list_new_G,int(7))
    C_stars_coordinate_list_new = cut(stars_coordinate_list_new_C,int(16))
    return G_ROTI_value,G_stars_coordinate_list_new,C_ROTI_value,C_stars_coordinate_list_new,total_time

#读取数据
def read_data(file_path,filepath_sa,figure_save_path,stat_time, end_time,ss):
    file_time, file_data_list, all_time = [], [], []
    files = os.listdir(file_path)
    for root, dirs, files in os.walk(file_path):
        file_new = os.path.join(file_path, root)
        for file_one in files:
            root_new = os.listdir(root)
            ephem_country = root_new[-1].split('_')[0][6:]
            ephem_source = root_new[-1].split('_')[1]
            ephem_intertime = root_new[-1].split('_')[3]
            ephem_type = root_new[-1].split('_')[-1].split('.')[0]
            ephem_style = root_new[-1].split('_')[-1].split('.')[-1]
            station_name = root_new[1].split('_')[0][:4]
            MR = root_new[1].split('_')[0][4:6]
            obs_country = root_new[1].split('_')[0][6:]
            obs_source = root_new[1].split('_')[1]
            obs_interval_mimute = root_new[1].split('_')[3]
            obs_interval_second = root_new[1].split('_')[4]
            obs_type = root_new[1].split('_')[5].split('.')[0]
            for d in range(len(root_new)):
                file_obs = (os.path.join(file_new, root_new[d])).replace('\\', '//')
                if file_obs.split('.')[-1] == '23o':
                    file_data = open(file_obs, 'r').read().split('END OF HEADER')[1].split('>')  # 打开文件
                    for i in range(len(file_data)):
                        file_data_new = file_data[i].split('\n')
                        for j in range(1, len(file_data_new) - 1):
                            file_data_list.append(file_data_new[j])
                            time = file_data_new[0].split('\n')[0][1:5] + '-' + file_data_new[0].split('\n')[0][6:8] + '-' + \
                                   file_data_new[0].split('\n')[0][9:11] + ' ' + file_data_new[0].split('\n')[0][12:14] + ':' + \
                                   file_data_new[0].split('\n')[0][15:17] + ':' + file_data_new[0].split('\n')[0][18:20]
                            if time.split(':')[-1][0] == ' ':
                                time_new = time.split(':')[-1][0].replace(' ', '0')
                                time_new1 = file_data_new[0].split('\n')[0][1:5] + '-' + file_data_new[0].split('\n')[0][6:8] + '-' + \
                                            file_data_new[0].split('\n')[0][9:11] + ' ' + file_data_new[0].split('\n')[0][12:14] + ':' + \
                                            file_data_new[0].split('\n')[0][15:17] + ':' + time_new + \
                                            file_data_new[0].split('\n')[0][19:20]
                                file_time.append(time_new1)
                            else:
                                file_time.append(time)
            new_file_tiem = list(dict.fromkeys(file_time))  # 数据时间点去重
            for h in range(len(new_file_tiem)):
                if h % 600 == 0:
                    all_time.append(new_file_tiem[h])
            if datetime.datetime.strptime(stat_time.split(' ')[0], "%Y-%m-%d") <= datetime.datetime.strptime(str('2023-01-02 8:00:00').split(' ')[0], "%Y-%m-%d") and \
                    datetime.datetime.strptime(str('2023-01-02 8:00:00').split(' ')[0], "%Y-%m-%d") <= datetime.datetime.strptime(end_time.split(' ')[0], "%Y-%m-%d"):
                params = {'station_name': station_name, 'MR': MR, 'obs_country': obs_country, 'obs_source': obs_source,
                          'obs_interval_mimute': obs_interval_mimute, 'obs_interval_second': obs_interval_second,
                          'obs_type': obs_type, 'ephem_country': ephem_country, 'ephem_source': ephem_source,
                      'ephem_intertime': ephem_intertime, 'ephem_type': ephem_type,'ephem_style': ephem_style}
                G_ROTI_value,G_stars_coordinate_list_new,C_ROTI_value,C_stars_coordinate_list_new,total_time = get_data(file_data_list, params, filepath_sa, stat_time, end_time, all_time)
                get_ficture(G_ROTI_value,G_stars_coordinate_list_new,C_ROTI_value,C_stars_coordinate_list_new,total_time,figure_save_path,ss)
            else:
                print("该时间段范围内不存在ROTI数据！！！！！")
#主函数
def main(file_path,filepath_sa,figure_save_path,stat_time,end_time):
    ss = time.time()
    read_data(file_path, filepath_sa, figure_save_path, stat_time, end_time,ss)
    ee = time.time()
    print("代码运行时间：", int(ee) - int(ss))

if __name__ == '__main__':
#
    # filepath_ob = "..//Satellite_data//"  #星历数据文件路径
    # filepath_sa = "..//Ephemeris_data//"
    # figure_save_path = "..//picture//"
    # stat_time = "2023-01-01 00:00:00"
    # end_time = "2023-01-05 00:20:00"

    filepath_ob = sys.argv[1]
    filepath_sa = sys.argv[2]
    figure_save_path = sys.argv[3]
    stat_time = sys.argv[4]
    end_time = sys.argv[5]

    main(filepath_ob,filepath_sa,figure_save_path,stat_time,end_time)