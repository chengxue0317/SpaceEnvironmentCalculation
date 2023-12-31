Program ap8ae8
    implicit none

    integer(kind = 4), parameter :: NTIME_MAX = 100000 ! generated by Make File
    integer(kind = 4) ntmax, whichm, whatf, nene
    real(kind = 8) energy(2, 25)!, BBo(ntime_max), L(ntime_max), flux(NTIME_MAX,25)
    real(kind = 8), dimension(NTIME_MAX) :: BB0, Lm
    real(kind = 4), dimension(NTIME_MAX) :: lat, lon
    real(kind = 8), dimension(NTIME_MAX, 25) :: flux
    character(len = 200) :: FileName = 'BB0LM.txt'
    character(len = 19), dimension(NTIME_MAX) :: date, time
    integer i, j, lineCounts
    real temp_real

    open(unit = 100, file = FileName)
    read(100, *) lineCounts
    read(100, *) whatf
    read(100, *) whichm
    read(100, *)
    ntmax = lineCounts

    do i = 1, lineCounts
        read(100, *) temp_real, BB0(i), Lm(i), lat(i), lon(i), date(i), time(i)
    end do
    Lm = abs(Lm)
    close(100)

    select case(whichm)
    case(1, 2)
        nene = 2
        energy(1, 1:2) = (/E_start, E_end/)
        energy(2, 1:2) = (/E_start, E_end/)
    case(3, 4)
        nene = 2
        energy(1, 1:2) = (/E_start, E_end/)
        energy(2, 1:2) = (/E_start, E_end/)
    end select

    call get_AE8_AP8_flux(ntmax, whichm, whatf, nene, energy, BB0, Lm, flux)

    open(unit = 100, file = 'flux.txt')
    do i = 1, lineCounts
        write(100, "(2(g0,:,', '),A10,1x,A8,:,', ',*(g0,:,', '))") lat(i), lon(i), date(i), time(i), flux(i, 1:nene)
    end do
    close(100)

    ! GET_AE8_AP8_FLUX
    ! Description:
    ! This function allows one to compute NASA AE8 min/max and AP8 min/max flux for any B/Bo, L position.
    ! The output can be differential flux or flux within an energy range or integral flux.
    ! 这个函数允许你计算任意B/Bo，L位置，NASA AE8/AP8模式的值
    ! 输出可以是微分、能量范围内、或是积分通量

    ! Inputs:
    ! ntime:
    ! long integer number of points in arrays (max allowed is GET_IRBEM_NTIME_MAX())
    ! 数组中点的长整数（最大值为 GET_IRBEM_NTIME_MAX() 这个函数的返回值

    ! whichm:
    ! long integer to select in which NASA model to fly
    ! 用以选择使用哪个NASA模式飞行的长整数
    ! whichm		Model
    ! 1             AE8 MIN 太阳平静期电子通量
    ! 2             AE8 MAX 太阳活跃期电子通量
    ! 3             AP8 MIN 太阳平静期质子通量
    ! 4             AP8 MAX 太阳活跃期质子通量
    ! -1	 	    AE8 MIN - ESA Interpolation
    ! -2	 	    AE8 MAX - ESA Interpolation
    ! -3	 	    AP8 MIN - ESA Interpolation
    ! -4	 	    AP8 MAX - ESA Interpolation
    ! The ESA Interpolation scheme provides for better flux interpolations at low altitudes.
    ! See: E. J. Daly, et al., "Problems with models of the radiation belts",
    ! IEEE Trans. Nucl. Sci, Vol 43, No. 2, Apr. 1996. DOI 10.1109/23.490889

    ! whatf:
    ! long integer to select what flux to compute
    ! 用于选择计算什么类型辐射的长整数
    ! 1 - differential flux (MeV-1 cm-2 s-1) at energy(1,*)
    !     变量energy第一维取值的微分通量，单位：每兆电子伏 每平方厘米 每秒
    ! 2 - flux within an ernergy range (MeV-1 cm-2 s-1) - energy(1,*) to energy(2,*)
    !     变量energy第一维第二维范围内的通量，单位同上
    ! 3 - integral flux (cm-2 s-1) at energy(1,*)
    !     变量energy第一维取值以上范围内的积分通量，单位同上

    ! Nene:
    ! long integer number of energies in array energy (max allowed is 25)
    ! energy数组中能量的长整型，最大值为25
    ! 注：这个变量推测为选择某个通道来进行计算

    ! energy:
    ! array(2,25) of double. If whatf=1 or 3 then energy(2,*) is not considered.
    ! Note: if energy is in MeV then differential flux will be per MeV.
    ! double型的数组，形状为2×25。如果whatf = 1 or 3，那么energy的第二维中的数字不需要考虑
    ! 注意：如果energy是以兆电子伏为单位，那么微分通量单位将为每兆电子伏

    ! BBo:
    ! array(GET_IRBEM_NTIME_MAX()) of double.
    ! Provide B/Bequator for all position where to compute the fluxes.
    ! Note that the Jensen and Cain 1960 magnetic field model must be used for any call to
    ! AE8min, AE8max, AP8min and GSFC 1266 (extended to year 1970) for any call to AP8max.
    ! double型的一维数组，数组下标最大取值为函数GET_IRBEM_NTIME_MAX()的返回值
    ! 为计算通量的所有位置提供B/Bequator的值
    ! 注意：任何调用AE8min, AE8max, AP8min和GSFC 1266(扩展到1970年)都必须使用Jensen和Cain 1960磁场模型。

    ! L:
    ! array(GET_IRBEM_NTIME_MAX()) of double.
    ! Provide McIlwain L for all position where to compute the fluxes.
    ! Note that the Jensen and Cain 1960 magnetic field model must be used for any call to
    ! AE8min, AE8max, AP8min and GSFC 1266 (extended to year 1970) for any call to AP8max.
    ! double型的一维数组，数组下标最大取值为函数GET_IRBEM_NTIME_MAX()的返回值
    ! 为计算通量的所有位置提供McIlwain L。
    ! 注意：同上

    ! Outputs:
    ! flux:
    ! flux according to selection of whatf for all times and energies (array(GET_IRBEM_NTIME_MAX(),25) of double)
    ! 粒子通量，取决于对时间和能级的选择，doubgle型数组，维度形状为 函数array(GET_IRBEM_NTIME_MAX()返回值×25

    ! CALLING SEQUENCE FROM MATLAB:
    ! Flux = onera_desp_lib_get_ae8_ap8_flux(whichm,energy,BBo,L)
    ! CALLING SEQUENCE from IDL:
    ! result = call_external(lib_name, 'get_ae8_ap8_flux_idl_', ntime,whichm,whatf,Nene,energy,BBo,L,,flux, /f_value)
    ! CALLING SEQUENCE from FORTRAN:
    ! CALL get_ae8_ap8_flux (ntime,whichm,whatf,Nene,energy,BBo,L,flux )


    ! do i = 1, 100
    !     if ( flux(i,1) .ge. 999999.9999999 ) then
    !         print"(es14.7)", flux(i,1)
    !     else
    !         print"(f14.7)", flux(i,1)
    !     endif
    ! end do

End Program ap8ae8