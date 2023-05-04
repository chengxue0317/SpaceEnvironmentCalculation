# -*- coding: utf8 -*-
import math
from pytz import utc
import datetime
import numpy as np

sys_simplify = {'GPS': 'G', 'GLONASS': 'R', 'SBAS': 'S', 'GAL': 'E', 'BDS': 'C', 'QZSS': 'J', 'IRNSS': 'I'}
ang = 180/math.pi
a, b = 6378137.0000, 6356752.3142
e = math.sqrt(1-(b/a)**2)
e2 = e*e

class ephemeris_data_analysis(object):
    def __init__(self, param, file_path):
        self.filepath = file_path
        self.params = param

    def param_analysis(self):
        tm = self.params['time']
        time = datetime.datetime.strptime(tm, "%Y-%m-%d %H:%M:%S")
        self.tms_utc = time.astimezone(utc).strftime('%Y%m%d%H%M%S')
        self.sat_num = self.params['system'] + ' ' +self.params['PRN']
        self.interval = float(self.params['interval'])
        self.forecast_period = float(self.params['Forecast Period'])
        self.station_name = self.params['station_name']
        self.MR = self.params['MR']
        self.obs_country = self.params['obs_country']
        self.obs_source = self.params['obs_source']
        self.obs_interval_mimute = self.params['obs_interval_mimute']
        self.obs_interval_second = self.params['obs_interval_second']
        self.obs_type = self.params['obs_type']
        self.ephem_country = self.params['ephem_country']
        self.ephem_source = self.params['ephem_source']
        self.ephem_intertime = self.params['ephem_intertime']
        self.ephem_type = self.params['ephem_type']
        self.ephem_style = self.params['ephem_style']

    def cac_daystoyear(self):
        year, month, day, hour, minute = self.tms_utc[:4], self.tms_utc[4:6], self.tms_utc[6:8], self.tms_utc[8:10], self.tms_utc[10:12]
        target_day = datetime.date(int(year), int(month), int(day))
        day_count = target_day - datetime.date(target_day.year - 1, 12, 31)
        daycount = str(day_count.days).zfill(3)  # 年积日
        obs_file, bro_file = '', ''
        if self.ephem_style == 'rnx':
            obs_file = '{}{}{}_{}_{}{}{}{}_{}_{}_{}.{}o'.format(self.station_name, self.MR, self.obs_country, self.obs_source, year, daycount, hour, minute, self.obs_interval_mimute, self.obs_interval_second, self.obs_type, year[-2:])
            bro_file = 'BRDM{}{}_{}_{}{}{}{}_{}_{}.{}'.format(self.MR, self.ephem_country, self.ephem_source, year, daycount, hour, minute, self.ephem_intertime, self.ephem_type, self.ephem_style)

        if self.ephem_style == 'n':
            obs_file = '{}{}0.{}o'.format(self.station_name, daycount, year[-2:])
            bro_file = 'brdm{}0.{}n'.format(daycount, year[-2:])
        return obs_file, bro_file

    def E_optimize(self, M_k, e):
        E0, E1 = 0, M_k
        while abs(E0 - E1) > 1e-10:
            E0 = E1
            E1 = M_k + e * math.sin(E0)
        return E1

    def cac_zenith_angle(self, station, satellite):
        N0, E0, U0 = station[:]
        z_angle = []
        for i in satellite:
            N, E, U = i[:]
            zenith = 90 - math.sin((U - U0) / math.sqrt(sum(np.array([N - N0, E - E0, U - U0]) ** 2))) * 180 / math.pi
            z_angle.append(zenith)
        return z_angle

    def read_file(self, file):
        try:
            f_obs = open(file, 'rt')
            lines = f_obs.readlines()
            f_obs.close()
            lst = [x.rstrip() for x in lines]
            return lst
        except:
            return False  # 没有指定时间的文件

    def header_data(self, ds):
        self.X, self.delta_H = '', ''
        for i in range(len(ds)):
            datas = ds[i].split()
            if datas[-1] == 'H/E/N':
                self.delta_H, self.E, self.N = float(datas[0]), float(datas[1]), float(datas[2])
            if datas[-1] == 'XYZ':
                self.X, self.Y, self.Z = float(datas[0]), float(datas[1]), float(datas[2])
            if self.X != '' and self.delta_H != '':
                break

    def time_format(self, ss, tm):
        str_time = datetime.datetime.strptime(tm, '%Y%m%d%H%M%S').strftime('%Y %m %d %H %M %S')
        time = ss + str_time
        return time

    def time_list(self, list):
        self.sn = self.sat_num.split(' ')
        self.comparison_code = sys_simplify[self.sn[0]] + self.sn[1] + ' ' + self.time_format('> ', self.tms_utc)[2:]
        for i in range(len(list)):
            ds = list[i][:20]
            if self.comparison_code[:-3] == ds:
                break
            elif self.comparison_code[:-6] == ds[:-3] and abs(float(self.tms_utc[-4:-2]) - float(ds[18:20])) <= 15:
                self.tms_utc = ds[4:8] + ds[9:11] + ds[12:14] + ds[15:17] + ds[18:20] + list[i][21:23]
                break

        new_list = datetime.datetime.strptime(self.tms_utc, '%Y%m%d%H%M%S')-datetime.timedelta(seconds=(3600//self.interval-1)*self.interval)
        self.forecast_list = []
        for i in range(int(self.forecast_period//self.interval)):
            tm = (new_list + datetime.timedelta(seconds=self.interval*i)).strftime('%Y%m%d%H%M%S')
            self.forecast_list.append(tm)

    def cac_ARP(self):
        x, y, z = self.X, self.Y, self.Z
        lbh = self.xyz_to_llh([[x, y, z]])
        self.blh_to_xyz(lbh)

    def blh_to_xyz(self, lbh):
        L, B, H = lbh[0][0]+self.E, lbh[0][1]+self.N, lbh[0][2]+self.delta_H
        n = a/math.sqrt(1-e2*math.sin(B/ang)**2)  # B纬度
        self.aprx = (n+H)*math.cos(B/ang)*math.cos(L/ang)  # 天线的x坐标 绝对值
        self.apry = (n+H)*math.cos(B/ang)*math.sin(L/ang)  # 天线的y坐标 绝对值
        self.aprz = (n*(1.0-e2)+H)*math.sin(B/ang)  # 天线的z坐标

    def xyz_to_neu(self, xyz):
        xyz = np.array(xyz)
        x, y, z = xyz[:, 0], xyz[:, 1], xyz[:, 2]
        x2, y2, z2 = x**2, y**2, z**2
        E2, F = a**2 - b**2, 54*b**2*z2
        neu_coordinate = []
        for i in range(len(x)):
            r = math.sqrt(x2[i] + y2[i])
            G = r**2 + (1-e2)*z2[i] - e2*E2
            c = (e2*e2*F[i]*r**2)/(G*G*G)
            s = (1 + c + math.sqrt(c*c + 2*c))**(1/3)
            P = F[i]/(3*(s+1/s+1)**2 * G*G)
            Q = math.sqrt(1+2*e2*e2*P)
            ro = -(P*e2*r)/(1+Q) + math.sqrt((a*a/2)*(1+1/Q)-(P*(1-e2)*z2[i])/(Q*(1+Q)) - P*r**2/2)
            tmp = (r-e2*ro)**2
            V = math.sqrt(tmp + (1-e2)*z2[i])
            zo = (b**2*z[i])/(a*V)
            lati = math.atan((z[i]+(e*(a/b))**2*zo)/r)
            long = math.atan2(y[i], x[i])
            sinb, cosb = math.sin(lati), math.cos(lati)
            sinl, cosl = math.sin(long), math.cos(long)
            N = -sinb*cosl*x[i]-sinb*sinl*y[i]+cosb*z[i]
            E = -sinl*x[i]+cosl*y[i]
            U = cosb*cosl*x[i]+cosb*sinl*y[i]+sinb*z[i]
            neu_coordinate.append([N, E, U])
        return neu_coordinate

    def xyz_to_llh(self, xyz):
        xyz = np.array(xyz)
        x, y, z = xyz[:, 0], xyz[:, 1], xyz[:, 2]
        x2, y2, z2 = x**2, y**2, z**2
        E2, F = a**2-b**2, 54*b**2*z2
        geodetic_coordinate, subsatellite_point_coordinate = [], []
        for i in range(len(x)):
            r = math.sqrt(x2[i]+y2[i])
            G = r**2+(1-e2)*z2[i]-e2*E2
            c = (e2*e2*F[i]*r**2)/(G**3)
            s = (1+c+math.sqrt(c**2+2*c))**(1/3)
            P = F[i]/(3*(s+1/s+1)**2*G**2)
            Q = math.sqrt(1+2*e2**2*P)
            ro = -(P*e2*r)/(1+Q)+math.sqrt((a*a/2)*(1+1/Q)-(P*(1-e2)*z2[i])/(Q*(1+Q))-P*r**2/2)
            tmp = (r-e2*ro)**2
            U = math.sqrt(tmp+z2[i])
            V = math.sqrt(tmp+(1-e2)*z2[i])
            zo = (b**2*z[i])/(a*V)
            high = U*(1-b**2/(a*V))
            lati = math.atan((z[i]+(e*(a/b))**2*zo)/r)
            long = math.atan2(y[i], x[i])
            geodetic_coordinate.append([long * ang, lati * ang, high])
        return geodetic_coordinate

    def bro_parma_read(self, list):
        if self.sn[0] in ['GLONASS', 'SBAS']:
            a = -2
        else:
            a = 0
        n_dic, tmx = [], ''
        for i in range(len(list)):
            ds = list[i][:20]
            if self.comparison_code[:-3] == ds:
                data_content = list[i].strip()
                n_dic.append([data_content[0:3], data_content[4:23], float((data_content[23:38] + 'e' + data_content[39:42])),
                     float((data_content[42:57] + 'e' + data_content[58:61])), float((data_content[61:76] + 'e' + data_content[77:80]))])
                for j in range(1, 6 + a):
                    data_content = list[i + j]
                    n_dic.append([float((data_content[4:19] + 'e' + data_content[20:23])),
                                  float((data_content[23:38] + 'e' + data_content[39:42])),
                                  float((data_content[42:57] + 'e' + data_content[58:61])),
                                  float((data_content[61:76] + 'e' + data_content[77:80]))])
                tx = list[i + 4][:23].strip()
                tmx = tx[4:8] + tx[9:11] + tx[12:14] + tx[15:17] + tx[18:20] + tx[21:23]
                break
        n_data = sum(n_dic, [])
        return n_data, tmx

    def cac_GPS_seconeds(self, utc):
        year, month, day, hour, minute, second = int(utc[:4]), int(utc[4:6]), int(utc[6:8]), int(utc[8:10]), int(
            utc[10:12]), int(utc[12:14])
        if month <= 2:
            year = year - 1
            month = month + 12  # 1，2月视为前一年13，14月
        # GPS时起始时刻1980年1月6日0点, year是两位数,需要转换到四位数
        # 需要将当前需计算的时刻先转换到儒略日再转换到GPS时间
        UT = hour + (minute / 60.0) + (second / 3600)
        JD = int(365.25 * year) + int(30.6001 * (month + 1)) + day + UT / 24 + 1720981.5
        WN = int((JD - 2444244.5) / 7)  # WN:目标时刻的GPS周
        WD = JD - 2444244.5 - 7.0 * WN
        t_oc = WD * 24 * 3600.0  # 目标时刻的GPS秒
        return t_oc, WN, WD

    def params_cac(self, list): # list_bro, SatNum, tms_utc
        #  计算接收天线的实际坐标xyz
        self.cac_ARP()
        dis, location = {}, []
        if self.sn[0] not in ['GLONASS', 'SBAS']:
            datas, tmx = self.bro_parma_read(list)
            if len(datas) == 0:
                raise  # 没有对应时间、对应的卫星系统的卫星编号
            a_0, a_1, a_2 = datas[2], datas[3], datas[4]  # 星历数据 数据2-4
            IODE, C_rs, dn, M0 = datas[5], datas[6], datas[7], datas[8]  # 星历数据 广播轨道–1
            C_uc, e, C_us, sqrt_A = datas[9], datas[10], datas[11], datas[12]  # 星历数据 广播轨道–2
            Toe, C_ic, OMEGA0, C_is = datas[13], datas[14], datas[15], datas[16]  # 星历数据 广播轨道–3
            I_0, C_rc, w, OMEGA_DOT = datas[17], datas[18], datas[19], datas[20]  # 星历数据 广播轨道–4
            I_DOT = datas[21]  # 星历数据 广播轨道–5

            for tm in self.forecast_list:
                # 1.计算卫星运行平均角速度 GM:WGS84下的引力常数 =3.986005e14，a:长半径
                GM = 3.986005e14
                n_0 = math.sqrt(GM) / sqrt_A ** 3
                n = n_0 + dn

                # 2.计算归化时间t_k 计算t时刻的卫星位置  UT：世界时 此处以小时为单位, t1为观测时刻
                t1, WN, WD = self.cac_GPS_seconeds(tm)
                dt = a_0 + a_1 * (t1 - Toe) + a_2 * (t1 - Toe) ** 2
                t = t1 - dt
                t_k = t - Toe
                if t_k > 302400:
                    t_k -= 604800
                elif t_k < -302400:
                    t_k += 604800
                else:
                    t_k = t_k

                # 3.平近点角计算M_k = M_0+n*t_k
                M_k = M0 + n * t_k  # 实际应该是乘t_k

                # 4.偏近点角计算 E_k  (迭代计算) E_k = M_k + e*sin(E_k)
                E = self.E_optimize(M_k, e)

                # 5.计算卫星的真近点角
                A1 = math.sqrt(1 - e * e) * math.sin(E)
                A2 = math.cos(E) - e
                V_k = math.atan2(A1, A2)

                # 6.计算升交距角 u_0(φ_k), ω:卫星电文给出的近地点角距
                u_0 = V_k + w

                # 7.摄动改正项 δu、δr、δi :升交距角u、卫星矢径r和轨道倾角i的摄动量
                du = C_uc * math.cos(2 * u_0) + C_us * math.sin(2 * u_0)
                dr = C_rc * math.cos(2 * u_0) + C_rs * math.sin(2 * u_0)
                di = C_ic * math.cos(2 * u_0) + C_is * math.sin(2 * u_0)

                # 8.计算经过摄动改正的升交距角u_k、卫星矢径r_k和轨道倾角 i_k
                u = u_0 + du
                r = sqrt_A ** 2 * (1 - e * math.cos(E)) + dr
                i = I_0 + di + I_DOT * t_k

                # 9.计算卫星在轨道平面坐标系的坐标,卫星在轨道平面直角坐标系（X轴指向升交点）中的坐标为：
                x_k = r * math.cos(u)
                y_k = r * math.sin(u)

                # 10.观测时刻升交点经度Ω_k的计算，升交点经度Ω_k等于观测时刻升交点赤经Ω与格林尼治恒星时GAST之差  Ω_k=Ω_0+(ω_DOT-omega_e)*t_k-omega_e*t_oe
                omega_e = 7.29211567e-5  # 地球自转角速度
                OMEGA_k = OMEGA0 + (OMEGA_DOT - omega_e) * t_k - omega_e * Toe  # 星历中给出的Omega0即为Omega_o=Omega_t_oe-GAST_w

                # 11.计算卫星在地固系中的直角坐标l
                X_k = x_k * math.cos(OMEGA_k) - y_k * math.cos(i) * math.sin(OMEGA_k)
                Y_k = x_k * math.sin(OMEGA_k) + y_k * math.cos(i) * math.cos(OMEGA_k)
                Z_k = y_k * math.sin(i)
                # 12.计算星地几何距离
                if X_k != 0 and Y_k != 0 and Z_k != 0:
                    location.append([X_k, Y_k, Z_k])
                    d = math.sqrt((X_k - self.aprx) ** 2 + (Y_k - self.apry) ** 2 + (Z_k - self.aprz) ** 2)
                    dis[tm] = d
        else:
            read_tm, tm, disx, dis = [utm], utm, [], {}
            for i in range(len(self.forecast_list)):
                datas, tm = self.bro_parma_read(list)
                read_tm.append(tm)
                X_k, Y_k, Z_k = datas[5] * 1000, datas[9] * 1000, datas[13] * 1000
                # 12.计算星地几何距离
                if X_k != 0 and Y_k != 0 and Z_k != 0:
                    location.append([X_k, Y_k, Z_k])
                    d = math.sqrt((X_k - x_s) ** 2 + (Y_k - y_s) ** 2 + (Z_k - z_s) ** 2)
                    disx.append(d)
            dis = dict(zip(read_tm, disx))
            self.forecast_list = read_tm

        #  计算卫星、地面接收设备的大地坐标
        if len(location) != 0:
            station_coordinate = self.xyz_to_llh([[self.aprx, self.apry, self.aprz]])
            station_neu_coordinate = self.xyz_to_neu([[self.aprx, self.apry, self.aprz]])
            geodetic_coordinate = self.xyz_to_llh(location)
            neu_coordinate = self.xyz_to_neu(location)
            location_dict = dict(zip(self.forecast_list, location))
            geodetic_dict = dict(zip(self.forecast_list, geodetic_coordinate))
            zenith_angle = self.cac_zenith_angle(station_neu_coordinate[0], neu_coordinate)
            zenith_dict = dict(zip(self.forecast_list, zenith_angle))
            return location_dict, geodetic_dict, [self.aprx, self.apry, self.aprz], station_coordinate[0], dis, station_neu_coordinate[0], zenith_dict
        else:
            raise  # 星历数据异常 （'GLONASS', 'SBAS'系统的X、Y、Z坐标为0导致的异常）

    def data_analysis(self):
        self.param_analysis()
        obs_file, bro_file = self.cac_daystoyear()
        # print('观测文件名称：', obs_file)
        # print('星历文件名称：', bro_file)
        obs_list = self.read_file(self.filepath+obs_file)
        self.header_data(obs_list)
        bro_list = self.read_file(self.filepath+bro_file)
        self.time_list(obs_list)
        # 返回预报各时点的卫星地心地固坐标系坐标、星下点坐标1，接收站的地心地固系坐标、大地坐标, 星地几何距离、接收站站心坐标、天顶距、星下点坐标2
        # 地心地固坐标系坐标（X、Y、Z）， 单位：米，大地坐标（经度、纬度、高度），星地几何距离单位：米
        info = self.params_cac(bro_list)
        return info
#         print('卫星地心地固系坐标', info[0])
#         print('星下点坐标', info[1])
#         print('接收站地心地固系坐标', info[2])
#         print('接收站大地坐标系坐标', info[3])
#         print('星地几何距离', info[4])
#         print('接收站站心坐标', info[5])
#         print('天顶距', info[6])
#
# if __name__ == '__main__':
#     params = {'time':'2023-01-01 8:00:00', 'system': 'GLONASS', 'PRN': '11', 'interval': '600', 'Forecast Period': '10800',  # 时间为北京时
#               'station_name':'ABMF', 'MR':'00', 'obs_country':'GLP', 'obs_source':'S', 'obs_interval_mimute':'15M', 'obs_interval_second':'01S', 'obs_type':'MO',  # 地面观测站文件标识
#               'ephem_country':'DLR', 'ephem_source':'S', 'ephem_intertime':'01D', 'ephem_type':'MN', 'ephem_style':'rnx'} # 星历数据文件标识
#     # 'system' {'BDS', 'GAL', 'IRNSS', 'QZSS', 'GPS', 'GLONASS', 'SBAS'}
#     # filepath = r'E:/公司文件/20220901-低轨目标跟踪项目-实施/code/satellite_data_new/data/'
#     filepath = '..//data//'
#     ephemeris_data = ephemeris_data_analysis(params, filepath)
#     ephemeris_data.data_analysis()