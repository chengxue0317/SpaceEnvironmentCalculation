import numpy as np
from time import sleep
from selenium import webdriver
from selenium.webdriver.common.by import By
import os


def get_chrome_driver():
    options = webdriver.ChromeOptions()
    options.add_argument("headless")
    options.add_argument('--no-sandbox')
    options.add_argument('--disable-gpu')
    options.add_argument('--disable-dev-shm-usage')
    return webdriver.Chrome(options=options)


def download():
	inc_index = []
	h_index = []
	for file_name in os.listdir():
		inc_index.append(int(file_name.split('inc_')[1].split('h')[0]))
		h_index.append(int(file_name.split('h_')[1].split('.flx')[0]))
	inc_index = list(map(int,inc_index))
	h_index = list(map(int,h_index))
	inc_index = np.array(inc_index)
	h_index = np.array(h_index)
	ind = np.where(h_index==np.max(h_index))
	h_index = np.max(h_index)
	inc_index = np.max(inc_index[ind])


	driver = get_chrome_driver()
	driver.get(r'https://creme.isde.vanderbilt.edu/CREME-MC/dashboard')


	user_input = driver.find_element(by=By.XPATH, value='//input[@name="__ac_name"]')
	pw_input = driver.find_element(by=By.XPATH, value='//input[@type="password"]')
	login_btn = driver.find_element(by=By.XPATH, value='//input[@name="submit"]')

	# 输入用户名和密码，点击登录
	user_input.send_keys('yuechao')
	pw_input.send_keys('Qazqaz123,')
	login_btn.click()
	print('login success')
	my_folder = driver.find_element(by=By.XPATH, value='//a[@href="https://creme.isde.vanderbilt.edu/CREME-MC/Members/yuechao"]')
	my_folder.click()


	# 进入GTRN
	gtrn = driver.find_element(by=By.XPATH, value='//a[@href="Gtrn"]')
	gtrn.click()

	#选择Specify orbit: c
	c = driver.find_element(by=By.XPATH, value='//input[@value="3"]')
	c.click()

	#输入升交点赤经
	inclination = driver.find_element(by=By.XPATH, value='//input[@class="creme-mc-text GtrnTemplate-initialLongitude"]')
	inclination.send_keys('0')

	#输入Initialdisplacement
	Initialdisplacement = driver.find_element(by=By.XPATH, value='//input[@class="creme-mc-text GtrnTemplate-initialDisplacement"]')
	Initialdisplacement.send_keys('0')

	#输入displacementOfperigee
	displacementOfperigee = driver.find_element(by=By.XPATH, value='//input[@class="creme-mc-text GtrnTemplate-displacementOfPerigee"]')
	displacementOfperigee.send_keys('0')


	inc_value = np.arange(inc_index,91,2)
	height = np.arange(h_index,2001,50)
	for j in range(len(height)):
		for i in range(len(inc_value)):
			#输入远地点数
			apogee = driver.find_element(by=By.XPATH, value='//input[@class="creme-mc-text GtrnTemplate-apogee"]')
			apogee.send_keys(str(height[j]))

			#输入近地点数
			perigee = driver.find_element(by=By.XPATH, value='//input[@class="creme-mc-text GtrnTemplate-perigee"]')
			perigee.send_keys(str(height[j]))

			#输入倾角
			inclination = driver.find_element(by=By.XPATH, value='//input[@class="creme-mc-text GtrnTemplate-inclination"]')
			inclination.send_keys(str(inc_value[i]))


			#输入文件名
			filename = driver.find_element(by=By.XPATH, value='//input[@class="creme-mc-text GtrnTemplate-rootname"]')
			filename.send_keys('inc_'+str(inc_value[i])+'h_'+str(height[j]))

			#提交程序
			submit = driver.find_element(by=By.XPATH, value='//input[@name="form.button.submit"]')
			submit.click()
			print('gtrn_inc_'+str(inc_value[i])+'h_'+str(height[j])+ ' finished')

			#点击FLUX
			flux = driver.find_element(by=By.XPATH, value='//a[@href="Flux"]')
			flux.click()

			# 清除原子序数
			z_number = driver.find_element(by=By.XPATH, value='//input[@class="creme-mc-text FluxTemplate-z2"]')
			z_number.clear()

			# 输入原子序数
			z_number = driver.find_element(by=By.XPATH, value='//input[@class="creme-mc-text FluxTemplate-z2"]')
			z_number.send_keys('92')
			

			#点击Inside Earth's
			inside = driver.find_element(by=By.XPATH, value='//input[@value="Inside Magnetosphere"]')
			inside.click()


			#点击Browse GTRN文件
			gtrn_browse = driver.find_element(by=By.XPATH, value='//input[@id="gtrnFileId_button"]')
			gtrn_browse.click()

			# 步骤1：获取窗口句柄
			windows = driver.window_handles       # 返回的是一个句柄列表，列表排序为当前窗口顺序

			# 步骤2：切换到新窗口
			driver.switch_to.window(windows[-1])  # 新窗口通常为最后一个，若为其他位置则自行处理	

			#清除文件名
			filename = driver.find_element(by=By.XPATH, value='//input[@id="searchGadget"]')
			filename.clear()	
		
			#输入GTRN文件名
			gtrn_name = driver.find_element(by=By.XPATH, value='//input[@id="searchGadget"]')
			gtrn_name.send_keys('inc_'+str(inc_value[i])+'h_'+str(height[j]))

			#点击search
			gtrn_search = driver.find_element(by=By.XPATH, value='//input[@class="searchButton"]')
			gtrn_search.click()

			#点击select
			gtrn_select = driver.find_element(by=By.XPATH, value='//a[@href=" #"]')
			gtrn_select.click()

	

			#步骤1：获取窗口句柄
			windows = driver.window_handles       # 返回的是一个句柄列表，列表排序为当前窗口顺序

			# 步骤2：切换到新窗口
			driver.switch_to.window(windows[0])  # 新窗口通常为最后一个，若为其他位置则自行处理

			#清除文件名
			filename = driver.find_element(by=By.XPATH, value='//input[@name="rootname"]')
			filename.clear()

			#输入flux文件名
			flux_name = driver.find_element(by=By.XPATH, value='//input[@name="rootname"]')
			flux_name.send_keys('flux_solarmin_inc_'+str(inc_value[i])+'h_'+str(height[j]))		

			#点击submit
			submit = driver.find_element(by=By.XPATH, value='//input[@name="form.button.submit"]')
			submit.click()
			#sleep(5000)
			#下载flux结果
			download = driver.find_element(by=By.XPATH, value='//a[@href="DisplaySessionData?tag=File.tsv&download=1"]')
			download.click()
			print('flux_solarmin_inc_'+str(inc_value[i])+'h_'+str(height[j])+' finished')

			#后退
			driver.back()

			#选择太阳极大年
			solarmax = driver.find_element(by=By.XPATH, value='//input[@value="Solar Maximum"]')
			solarmax.click()

			#点击Browse GTRN文件
			gtrn_browse = driver.find_element(by=By.XPATH, value='//input[@id="gtrnFileId_button"]')
			gtrn_browse.click()

			# 步骤1：获取窗口句柄
			windows = driver.window_handles       # 返回的是一个句柄列表，列表排序为当前窗口顺序

			# 步骤2：切换到新窗口
			driver.switch_to.window(windows[-1])  # 新窗口通常为最后一个，若为其他位置则自行处理		
		
			#输入GTRN文件名
			gtrn_name = driver.find_element(by=By.XPATH, value='//input[@id="searchGadget"]')
			gtrn_name.send_keys('inc_'+str(inc_value[i])+'h_'+str(height[j]))

			#点击search
			gtrn_search = driver.find_element(by=By.XPATH, value='//input[@class="searchButton"]')
			gtrn_search.click()

			#点击select
			gtrn_select = driver.find_element(by=By.XPATH, value='//a[@href=" #"]')
			gtrn_select.click()

			# 步骤1：获取窗口句柄
			windows = driver.window_handles       # 返回的是一个句柄列表，列表排序为当前窗口顺序

			# 步骤2：切换到新窗口
			driver.switch_to.window(windows[0])  # 新窗口通常为最后一个，若为其他位置则自行处理

			#清除文件名
			filename = driver.find_element(by=By.XPATH, value='//input[@class="creme-mc-text FluxTemplate-rootname"]')
			filename.clear()

			#输入flux文件名
			flux_name = driver.find_element(by=By.XPATH, value='//input[@name="rootname"]')
			flux_name.send_keys('flux_solarmax_inc_'+str(inc_value[i])+'h_'+str(height[j]))		

			#点击submit
			submit = driver.find_element(by=By.XPATH, value='//input[@name="form.button.submit"]')
			submit.click()

			#下载flux结果
			download = driver.find_element(by=By.XPATH, value='//a[@href="DisplaySessionData?tag=File.tsv&download=1"]')
			download.click()
			print('flux_solarmax_inc_'+str(inc_value[i])+'h_'+str(height[j])+' finished')


			#后退
			driver.back()

			#选择太阳极大年
			Worstweek = driver.find_element(by=By.XPATH, value='//input[@value="Worst Week"]')
			Worstweek.click()

			#点击Browse GTRN文件
			gtrn_browse = driver.find_element(by=By.XPATH, value='//input[@id="gtrnFileId_button"]')
			gtrn_browse.click()

			# 步骤1：获取窗口句柄
			windows = driver.window_handles       # 返回的是一个句柄列表，列表排序为当前窗口顺序

			# 步骤2：切换到新窗口
			driver.switch_to.window(windows[-1])  # 新窗口通常为最后一个，若为其他位置则自行处理		
		
			#输入GTRN文件名
			gtrn_name = driver.find_element(by=By.XPATH, value='//input[@id="searchGadget"]')
			gtrn_name.send_keys('inc_'+str(inc_value[i])+'h_'+str(height[j]))

			#点击search
			gtrn_search = driver.find_element(by=By.XPATH, value='//input[@class="searchButton"]')
			gtrn_search.click()

			#点击select
			gtrn_select = driver.find_element(by=By.XPATH, value='//a[@href=" #"]')
			gtrn_select.click()

			# 步骤1：获取窗口句柄
			windows = driver.window_handles       # 返回的是一个句柄列表，列表排序为当前窗口顺序

			# 步骤2：切换到新窗口
			driver.switch_to.window(windows[0])  # 新窗口通常为最后一个，若为其他位置则自行处理

			#清除文件名
			filename = driver.find_element(by=By.XPATH, value='//input[@class="creme-mc-text FluxTemplate-rootname"]')
			filename.clear()

			#输入flux文件名
			flux_name = driver.find_element(by=By.XPATH, value='//input[@name="rootname"]')
			flux_name.send_keys('flux_worstweek_inc_'+str(inc_value[i])+'h_'+str(height[j]))		

			#点击submit
			submit = driver.find_element(by=By.XPATH, value='//input[@name="form.button.submit"]')
			submit.click()

			#下载flux结果
			download = driver.find_element(by=By.XPATH, value='//a[@href="DisplaySessionData?tag=File.tsv&download=1"]')
			download.click()
			print('flux_worstweek_inc_'+str(inc_value[i])+'h_'+str(height[j])+' finished')


			#返回
			driver.back()
			driver.back()

			#清除倾角
			inclination = driver.find_element(by=By.XPATH, value='//input[@class="creme-mc-text GtrnTemplate-inclination"]')
			inclination.clear()

			#清除高度
			apogee = driver.find_element(by=By.XPATH, value='//input[@class="creme-mc-text GtrnTemplate-apogee"]')
			apogee.clear()
			perigee = driver.find_element(by=By.XPATH, value='//input[@class="creme-mc-text GtrnTemplate-perigee"]')
			perigee.clear()


			#清除文件名
			filename = driver.find_element(by=By.XPATH, value='//input[@class="creme-mc-text GtrnTemplate-rootname"]')
			filename.clear()
		inc_value = np.arange(0,100)

for i in range(100000000):
	try:
		download()
	except:
		a = 1