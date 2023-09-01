import numpy as np
import math
from matplotlib import pyplot as plt

'''
根据开普勒根数计算空间目标星下点，包括经度、纬度和高度
a：半长轴(m)
i：倾角(°)
e：偏心率(°)
Omega：升交点经度(°)
omega：近拱点角距(°)
sum_point：模拟数量点总量
step：模拟数据点间隔(秒)
'''


def Cal_subpoint(a, i, e, Omega, omega, sum_point, step):

    def Calculate_x(a, b, c):
        return (-b+math.sqrt(b*b-4*a*c))/(2*a), (-b-math.sqrt(b*b-4*a*c))/(2*a)

    # 一般情况：椭圆轨道的星下点计算
    pi = np.pi
    sin = np.sin
    cos = np.cos
    tan = np.tan
    k = 3.9861e14  # 地球引力常数
    we = 7.292e-5  # 地球自转角速度
    r_earth = 6371393  # 地球半径 m

    # 轨道参数 Degree
    a = a  # 半长轴
    i = i/180*pi  # 倾角
    e = e  # 偏心率
    Omega = Omega / 180 * pi  # 升交点经度
    omega = omega / 180 * pi  # 近拱点角距

    # 模拟参数
    total = sum_point  # 模拟数据点总量
    step = step  # 模拟数据点间隔 秒
    epsilon = 0.0001  # 牛顿迭代精度
    # data = f"半长轴 {a}m 倾角 {i}rad 离心率 {e} 升交点经度 {Omega}rad 进拱点角距 {omega}rad 数据点总量 {total} 点间隔 {step}"

    # 平均角速度n
    n = 1/((a**3/k)**0.5)
    theta, alt = [], []
    for t in range(0, step*total, step):
        # 平近点角M
        M = n*t
        # 牛顿迭代法解偏近点角E
        E0 = M
        E1 = M+1
        temp = E0
        while abs(E1-E0) > epsilon:
            E0 = temp
            E1 = E0 - (E0-M-e*sin(E0))/(1-e*cos(E0))
            temp = E1
        E = E1
        # 真近角theta
        theta_s = tan(2*np.arctan(((1+e)/(1-e))**0.5 * tan(E/2)))
        x1, x2 = Calculate_x(1-e*e+theta_s*theta_s, -2*a*e*(theta_s**2), (theta_s*a*e)**2+(e**2-1)*(a**2))
        y1 = theta_s*(x1-a*e)
        y2 = theta_s*(x2-a*e)
        if 2*np.arctan(((1+e)/(1-e))**0.5 * tan(E/2)) >= -pi/2 and 2*np.arctan(((1+e)/(1-e))**0.5 * tan(E/2)) <= pi/2:
            dis = min(math.sqrt((x1-a*e)**2+y1**2), math.sqrt((x2-a*e)**2+y2**2))-r_earth  # 米
        else:
            dis = max(math.sqrt((x1-a*e)**2+y1**2), math.sqrt((x2-a*e)**2+y2**2))-r_earth

        theta.append((2*np.arctan(((1+e)/(1-e))**0.5 * tan(E/2)), t))
        alt.append(round(dis/1000,3))

    # 纬度计算
    lat = []
    for (tt, _) in theta:
        lat.append(round(np.arcsin(np.sin(i) * np.sin(tt))/pi*180, 3))

    # 经度计算
    lon = []
    for (tt, t) in theta:
        co = 0
        temp = (tt+omega) % (2*np.pi)
        if temp > np.pi:
            temp = temp - 2*np.pi
        if temp < -0.5 * np.pi and temp > -1 * np.pi:
            co = -1 * np.pi
        if temp > 0.5 * np.pi and temp < 1 * np.pi:
            co = 1*np.pi
        temp1 = np.arctan(np.cos(i)*np.tan(tt+omega)) + Omega - we*t + co
        temp1 = temp1 % (2*np.pi)
        lon_s = temp1/pi*180
        if lon_s > 180:
            lon_s = lon_s-360
        lon.append(round(lon_s, 3))
    return lat, lon, alt


if __name__ == '__main__':
    lat, lon, alt = Cal_subpoint(8576000, 112.228, 0.241, 120.671, 19.6, 220, 40)
    print(lat)
    print(lon)
    print(alt)
