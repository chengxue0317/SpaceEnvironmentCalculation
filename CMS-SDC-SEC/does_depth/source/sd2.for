      PROGRAM SD2
C
C     SHIELDOSE-2, VERSION 2.10, 28 APR 94.
C
C        S.M. SELTZER
C        NATIONAL INSTITUTE OF STANDARDS AND TECHNOLOGY
C        GAITHERSBURG, MD 20899
C        (301) 975-5552
C
C        IDET = 1, AL DETECTOR
C               2, GRAPHITE DETECTOR
C               3, SI DETECTOR
C               4, AIR DETECTOR
C               5, BONE DETECTOR
C               6, CALCIUM FLUORIDE DETECTOR
C               7, GALLIUM ARSENIDE DETECTOR
C               8, LITHIUM FLUORIDE DETECTOR
C               9, SILICON DIOXIDE DETECTOR
C              10, TISSUE DETECTOR
C              11, WATER DETECTOR
C
C        INUC = 1, NO NUCLEAR ATTENUATION FOR PROTONS IN AL
C               2, NUCLEAR ATTENUATION, LOCAL CHARGED-SECONDARY ENERGY
C                     DEPOSITION
C               3, NUCLEAR ATTENUATION, LOCAL CHARGED-SECONDARY ENERGY
C                     DEPOSITION, AND APPROX EXPONENTIAL DISTRIBUTION OF
C                     NEUTRON DOSE
C
C        INCIDENT OMNIDIRECTIONAL FLUX IN /ENERGY/CM2/UNIT TIME
C             (SOLAR-FLARE FLUX IN /ENERGY/CM2).
C
C        EUNIT IS CONVERSION FACTOR FROM /ENERGY TO /MEV,
C             E.G., EUNIT = 1000 IF FLUX IS /KEV.
C
C        DURATN IS MISSION DURATION IN MULTIPLES OF UNIT TIME.
C
C     IMPLICIT DOUBLE PRECISION (A-H,O-Z)
      PARAMETER  (MMAXPI=133,KMAXPI=30,NMAXPI=49,LMAXPI=51,IMIXI=11,
     1   MMAXEI=81,NMAXEI=14,LMAXSI=33+1,LMAXEI=51,LMAXTI=37,LMAXBI=47,
     2   IMAXI=71,NPTSI=1001,JMAXI=301)
      PARAMETER  (NPTSPI=NPTSI,NPTSEI=NPTSI)
      PARAMETER  (ZCON=0.001*2.540005*2.70,ZMCON=10.0/2.70)
      CHARACTER  FILENM*40,PRTFIL*40,ARRFIL*40,TAG*72,DET(IMIXI)*8,
     1   VERSION*4
      DIMENSION  EP(MMAXPI),RP(MMAXPI),RPB(MMAXPI),RPC(MMAXPI),
     1   RPD(MMAXPI),TEPN(KMAXPI),FEPN(KMAXPI),FEPNB(KMAXPI),
     2   FEPNC(KMAXPI),FEPND(KMAXPI),TP(NMAXPI),ZRP(LMAXPI),
     3   DUM(LMAXPI),DALP(NMAXPI,LMAXPI),DALPB(NMAXPI,LMAXPI),
     4   DALPC(NMAXPI,LMAXPI),DALPD(NMAXPI,LMAXPI),
     5   DRATP(NMAXPI,LMAXPI),DRATPB(NMAXPI,LMAXPI),
     6   DRATPC(NMAXPI,LMAXPI),DRATPD(NMAXPI,LMAXPI)
      DIMENSION  EE(MMAXEI),RE(MMAXEI),REB(MMAXEI),REC(MMAXEI),
     1   RED(MMAXEI),YE(MMAXEI),YEB(MMAXEI),YEC(MMAXEI),YED(MMAXEI),
     2   TE(NMAXEI),AR(NMAXEI),ARB(NMAXEI),ARC(NMAXEI),ARD(NMAXEI),
     3   RS(NMAXEI),RSB(NMAXEI),RSC(NMAXEI),RSD(NMAXEI),BS(LMAXSI),
     4   ZRE(LMAXEI),ZS(LMAXTI),ZB(LMAXBI),DALE(NMAXEI,LMAXSI),
     5   DALEB(NMAXEI,LMAXSI),DALEC(NMAXEI,LMAXSI),DALED(NMAXEI,LMAXSI),
     6   DALB(NMAXEI,LMAXTI),DALBB(NMAXEI,LMAXTI),DALBC(NMAXEI,LMAXTI),
     7   DALBD(NMAXEI,LMAXTI),DRATE(NMAXEI,LMAXEI,2),
     8   DRATEB(NMAXEI,LMAXEI,2),DRATEC(NMAXEI,LMAXEI,2),
     9   DRATED(NMAXEI,LMAXEI,2),DRATB(NMAXEI,LMAXBI,2),
     X   DRATBB(NMAXEI,LMAXBI,2),DRATBC(NMAXEI,LMAXBI,2),
     1   DRATBD(NMAXEI,LMAXBI,2)
      DIMENSION  ZM(IMAXI),Z(IMAXI),ZMM(IMAXI),ZL(IMAXI),TPL(NPTSPI),
     1   TPP(NPTSPI),TEL(NPTSEI),ENEWT(NPTSPI),TEE(NPTSEI),RINE(NPTSEI),
     2   RINS(NPTSEI),ARES(NPTSEI),YLDE(NPTSEI),DIN(LMAXPI),
     3   DINB(LMAXPI),DINC(LMAXPI),DIND(LMAXPI),DRIN(LMAXPI),
     4   GP(NPTSPI,IMAXI),GE(NPTSEI,IMAXI,2),GB(NPTSEI,IMAXI,2),
     5   EPS(JMAXI),S(JMAXI),SOL(NPTSPI),SPG(NPTSPI),SEG(NPTSEI),
     6   G(NPTSI),DOSOL(IMAXI,2),DOSP(IMAXI,2),DOSE(IMAXI,2,2),
     7   DOSB(IMAXI,2,2)
      DATA  DET/'Aluminum','Graphite','Silicon','Air','Bone','CaF2',
     1   'GaAs','LiF','SiO2','Tissue','H2O'/
      DATA  ZMIN/1.0E-06/,RADCON/1.6021892E-08/,NBEGE/1/,ENMU/0.03/
      DATA  VERSION/'2.10'/
C      CALL LOGO (VERSION)
C      PRINT 10
   10 FORMAT (' Enter input filename: ')
C      READ 20,FILENM
   20 FORMAT (A)
      OPEN (UNIT=9,FILE='example_seu.inp')
      READ (9,20) PRTFIL
      OPEN (UNIT=10,FILE=PRTFIL)
      READ (9,20) ARRFIL
      OPEN (UNIT=12,FILE=ARRFIL)
      WRITE (10,30) VERSION
      WRITE (12,30) VERSION
   30 FORMAT (' OUTPUT FROM SHIELDOSE-2, VERSION ',A)
      WRITE (10,32) FILENM
      WRITE (12,32) FILENM
   32 FORMAT (' Input filename: ',A)
      WRITE (10,34) PRTFIL
      WRITE (12,34) PRTFIL
   34 FORMAT (' Print-out filename: ',A)
      WRITE (10,36) ARRFIL
      WRITE (12,36) ARRFIL
   36 FORMAT (' Array output filename: ',A)
      WRITE (10,370)
  370 FORMAT (/'  IDET  INUC  IMAX  IUNT')
      READ (9,*) IDET,INUC,IMAX,IUNT
      WRITE (10,380) IDET,INUC,IMAX,IUNT
  380 FORMAT (12I6)
      WRITE (12,380) IDET,IMAX,INUC
      INATT=2
      IF (INUC.EQ.1) INATT=1
      INEWT=0
      IF (INUC.EQ.3) INEWT=1
      PRINT *,' Reading database and preparing base arrays.............'
      PRINT *,'    Protons.............................................'
      OPEN (UNIT=11,FILE='PROTBAS2.DAT')
      READ (11,20) TAG
      READ (11,*) MMAXP,KMAXP,NMAXP,LMAXP,IMIX
      READ (11,*) (EP(M),M=1,MMAXP)
      READ (11,*) (RP(M),M=1,MMAXP)
      READ (11,*) (TEPN(K),K=1,KMAXP)
      READ (11,*) (FEPN(K),K=1,KMAXP)
      READ (11,*) (TP(N),N=1,NMAXP)
      READ (11,*) (ZRP(L),L=1,LMAXP)
      DO 50 N=1,NMAXP
      DO 38 I=1,2
      READ (11,*) (DUM(L),L=1,LMAXP)
      IF (I.NE.INATT) GO TO 38
      DO 390 L=1,LMAXP
  390 DALP(N,L)=DUM(L)
      DALP(N,LMAXP)=(DALP(N,LMAXP-1)/DALP(N,LMAXP-2))*DALP(N,LMAXP-1)
   38 CONTINUE
      DO 40 I=1,IMIX
      READ (11,*) (DUM(L),L=1,LMAXP)
      IF (I.NE.IDET) GO TO 40
      DO 39 L=1,LMAXP
   39 DRATP(N,L)=DUM(L)
   40 CONTINUE
   50 CONTINUE
      CLOSE (11)
      DO 60 M=1,MMAXP
      EP(M)=LOG(EP(M))
   60 RP(M)=LOG(RP(M))
      DO 65 K=1,KMAXP
   65 TEPN(K)=LOG(TEPN(K))
      DO 70 N=1,NMAXP
   70 TP(N)=LOG(TP(N))
      CALL SCOF (MMAXP,EP,RP,RPB,RPC,RPD)
      CALL SCOF (KMAXP,TEPN,FEPN,FEPNB,FEPNC,FEPND)
      DO 75 L=1,LMAXP
      DO 72 N=1,NMAXP
   72 DALP(N,L)=LOG(DALP(N,L))
      CALL SCOF (NMAXP,TP,DALP(1,L),DALPB(1,L),DALPC(1,L),DALPD(1,L))
   75 CONTINUE
      PRINT *,'    Electrons and bremsstrahlung........................'
      OPEN (UNIT=11,FILE='ELBRBAS2.DAT')
      READ (11,20) TAG
      READ (11,*) MMAXE,NMAXE,LMAXS,LMAXE,LMAXT,LMAXB,IMIX
      NLENE=NMAXE-NBEGE+1
      READ (11,*) (EE(M),M=1,MMAXE)
      READ (11,*) (RE(M),M=1,MMAXE)
      READ (11,*) (YE(M),M=1,MMAXE)
      READ (11,*) (TE(N),N=1,NMAXE)
      READ (11,*) (AR(N),N=1,NMAXE)
      READ (11,*) (RS(N),N=1,NMAXE)
      READ (11,*) (BS(L),L=1,LMAXS)
      BS(LMAXS+1)=2.0
      READ (11,*) (ZRE(L),L=1,LMAXE)
      READ (11,*) (ZS(L),L=1,LMAXT)
      READ (11,*) (ZB(L),L=1,LMAXB)
      DO 100 N=1,NMAXE
      READ (11,*) (DALE(N,L),L=1,LMAXS)
      DALE(N,LMAXS+1)=1.0E-07
      READ (11,*) (DALB(N,L),L=1,LMAXT)
      DO 90 I=1,IMIX
      DO 80 M=1,2
      READ (11,*) (DUM(L),L=1,LMAXE)
      IF (I.NE.IDET) GO TO 77
      DO 76 L=1,LMAXE
   76 DRATE(N,L,M)=DUM(L)
   77 READ (11,*) (DUM(L),L=1,LMAXB)
      IF (I.NE.IDET) GO TO 80
      DO 78 L=1,LMAXB
   78 DRATB(N,L,M)=DUM(L)
   80 CONTINUE
   90 CONTINUE
  100 CONTINUE
      LMAXS=LMAXS+1
      CLOSE (11)
      DO 110 M=1,MMAXE
      EE(M)=LOG(EE(M))
      RE(M)=LOG(RE(M))
  110 YE(M)=LOG(YE(M))
      DO 120 N=1,NMAXE
      TE(N)=LOG(TE(N))
      AR(N)=LOG(AR(N))
  120 RS(N)=LOG(RS(N))
      DO 130 L=1,LMAXB
  130 ZB(L)=LOG(ZB(L))
      CALL SCOF (MMAXE,EE,RE,REB,REC,RED)
      CALL SCOF (MMAXE,EE,YE,YEB,YEC,YED)
      CALL SCOF (NMAXE,TE,AR,ARB,ARC,ARD)
      CALL SCOF (NMAXE,TE,RS,RSB,RSC,RSD)
      DO 150 L=1,LMAXS
      DO 140 N=NBEGE,NMAXE
  140 DALE(N,L)=LOG(DALE(N,L))
      CALL LCOF (NLENE,TE(NBEGE),DALE(NBEGE,L),DALEB(NBEGE,L),
     1   DALEC(NBEGE,L),DALED(NBEGE,L))
C     CALL SCOF (NLENE,TE(NBEGE),DALE(NBEGE,L),DALEB(NBEGE,L),
C    1   DALEC(NBEGE,L),DALED(NBEGE,L))
  150 CONTINUE
      DO 170 L=1,LMAXT
      ZS(L)=LOG(ZS(L))
      DO 160 N=NBEGE,NMAXE
  160 DALB(N,L)=LOG(DALB(N,L))
      CALL LCOF (NLENE,TE(NBEGE),DALB(NBEGE,L),DALBB(NBEGE,L),
     1   DALBC(NBEGE,L),DALBD(NBEGE,L))
C     CALL SCOF (NLENE,TE(NBEGE),DALB(NBEGE,L),DALBB(NBEGE,L),
C    1   DALBC(NBEGE,L),DALBD(NBEGE,L))
  170 CONTINUE
C     PRINT *,' Preparing base arrays for selected detector material...'
      DO 220 L=1,LMAXP
      CALL SCOF (NMAXP,TP,DRATP(1,L),DRATPB(1,L),DRATPC(1,L),
     1   DRATPD(1,L))
  220 CONTINUE
      DO 240 M=1,2
      DO 230 L=1,LMAXE
      CALL LCOF (NLENE,TE(NBEGE),DRATE(NBEGE,L,M),
     1   DRATEB(NBEGE,L,M),DRATEC(NBEGE,L,M),DRATED(NBEGE,L,M))
C     CALL SCOF (NLENE,TE(NBEGE),DRATE(NBEGE,L,M),
C    1   DRATEB(NBEGE,L,M),DRATEC(NBEGE,L,M),DRATED(NBEGE,L,M))
  230 CONTINUE
  240 CONTINUE
      DO 260 M=1,2
      DO 250 L=1,LMAXB
      CALL LCOF (NLENE,TE(NBEGE),DRATB(NBEGE,L,M),
     1   DRATBB(NBEGE,L,M),DRATBC(NBEGE,L,M),DRATBD(NBEGE,L,M))
C     CALL SCOF (NLENE,TE(NBEGE),DRATB(NBEGE,L,M),
C    1   DRATBB(NBEGE,L,M),DRATBC(NBEGE,L,M),DRATBD(NBEGE,L,M))
  250 CONTINUE
  260 CONTINUE
      GO TO (440,470,500), IUNT
  440 WRITE (10,450)
  450 FORMAT (/' SHIELD DEPTH (mils)')
      READ (9,*) (ZM(I),I=1,IMAX)
      WRITE (10,455) (ZM(I),I=1,IMAX)
  455 FORMAT (1P6E12.5)
      DO 460 I=1,IMAX
      IF (ZM(I).LE.ZMIN/ZCON) ZM(I)=ZMIN/ZCON
      Z(I)=ZCON*ZM(I)
  460 ZMM(I)=Z(I)*ZMCON
      GO TO 530
  470 WRITE (10,480)
  480 FORMAT (/' SHIELD DEPTH (g/cm2)')
      READ (9,*) (Z(I),I=1,IMAX)
      WRITE (10,455) (Z(I),I=1,IMAX)
      DO 490 I=1,IMAX
      IF (Z(I).LE.ZMIN) Z(I)=ZMIN
      ZM(I)=Z(I)/ZCON
  490 ZMM(I)=Z(I)*ZMCON
      GO TO 530
  500 WRITE (10,510)
  510 FORMAT (/' SHIELD DEPTH (mm)')
      READ (9,*) (ZMM(I),I=1,IMAX)
      WRITE (10,455) (ZMM(I),I=1,IMAX)
      DO 520 I=1,IMAX
      IF (ZMM(I).LE.ZMIN*ZMCON) ZMM(I)=ZMIN*ZMCON
      Z(I)=ZMM(I)/ZMCON
  520 ZM(I)=Z(I)/ZCON
  530 DO 540 I=1,IMAX
  540 ZL(I)=LOG(Z(I))
      WRITE (12,1435) (Z(I),I=1,IMAX)
      WRITE (10,550)
  550 FORMAT(/'     EMINS     EMAXS     EMINP     EMAXP NPTSP     EMINE 
     1    EMAXE NPTSE')
      READ (9,*) EMINS,EMAXS,EMINP,EMAXP,NPTSP,EMINE,EMAXE,NPTSE
      WRITE (10,560) EMINS,EMAXS,EMINP,EMAXP,NPTSP,EMINE,EMAXE,NPTSE
  560 FORMAT (4F10.3,I6,2F10.3,I6)
      EMINU=MIN(EMINP,EMINS)
      EMAXU=MAX(EMAXP,EMAXS)
      DEP=LOG(EMAXU/EMINU)/FLOAT(NPTSP-1)
      EMINUL=LOG(EMINU)
      DELP=DEP/3.0
      CALL EINDEX (EMINU,DEP,NPTSP,EMINS,EMAXS,NFSTSB,NLSTSB,NLENSB)
      CALL EINDEX (EMINU,DEP,NPTSP,EMINP,EMAXP,NFSTPB,NLSTPB,NLENPB)
      DO 570 NP=1,NPTSP
      TPL(NP)=EMINUL+FLOAT(NP-1)*DEP
      TPP(NP)=EXP(TPL(NP))
  570 CONTINUE
      WRITE (10,580) TPP(NFSTSB),TPP(NLSTSB),TPP(NFSTPB),TPP(NLSTPB),
     1   NPTSP,EMINE,EMAXE,NPTSE
  580 FORMAT (4F10.3,I6,2F10.3,I6,'  ADJUSTED VALUES')
      WRITE (12,560) TPP(NFSTSB),TPP(NLSTSB),TPP(NFSTPB),TPP(NLSTPB),
     1   NPTSP,EMINE,EMAXE,NPTSE
      PRINT *,' Preparing mesh arrays to be integrated over spectra....'
      PRINT *,'    Protons.............................................'
      DO 660 NP=1,NPTSP
      CALL BSPOL (TPL(NP),MMAXP,EP,RP,RPB,RPC,RPD,ANS)
      RINP=EXP(ANS)
      DO 610 L=1,LMAXP
      IF (TPL(NP).LT.TP(NMAXP)) GO TO 590
      ANS=DALP(NMAXP,L)
      ANSR=DRATP(NMAXP,L)
      GO TO 605
  590 IF (TPL(NP).GT.TP(1)) GO TO 600
      ANS=DALP(1,L)
      ANSR=DRATP(1,L)
      GO TO 605
  600 CALL BSPOL (TPL(NP),NMAXP,TP,DALP(1,L),DALPB(1,L),
     1   DALPC(1,L),DALPD(1,L),ANS)
      ANSR=1.0
      IF (IDET.EQ.1) GO TO 605
      CALL BSPOL (TPL(NP),NMAXP,TP,DRATP(1,L),DRATPB(1,L),
     1   DRATPC(1,L),DRATPD(1,L),ANSR)
  605 DIN(L)=ANS+LOG(ANSR)
  610 CONTINUE
      ENEWT(NP)=0.0
      BENMU=0.0
      IF (INATT.EQ.1) GO TO 620
      IF (TPL(NP).LE.TEPN(1)) GO TO 615
      CALL BSPOL (TPL(NP),KMAXP,TEPN,FEPN,FEPNB,FEPNC,FEPND,ANS)
      ENEWT(NP)=TPP(NP)*ANS
  615 BENMU=ENEWT(NP)*ENMU
  620 CALL SCOF (LMAXP,ZRP,DIN,DINB,DINC,DIND)
      DO 650 I=1,IMAX
      ZRIN=Z(I)/RINP
      IF (ZRIN.LT.ZRP(LMAXP)) GO TO 640
      GP(NP,I)=0.0
      GO TO 645
  640 CALL BSPOL (ZRIN,LMAXP,ZRP,DIN,DINB,DINC,DIND,ANS)
      ANS=EXP(ANS)
      GP(NP,I)=TPP(NP)*ANS/RINP
  645 IF (INEWT.EQ.1.AND.TPL(NP).GT.TEPN(1)) GP(NP,I)=GP(NP,I)+
     1   BENMU*EXP(-ENMU*Z(I))
  650 CONTINUE
  660 CONTINUE
      PRINT *,'    Electrons and bremsstrahlung........................'
      EMINEL=LOG(EMINE)
      DEE=(LOG(EMAXE)-EMINEL)/FLOAT(NPTSE-1)
      DELE=DEE/3.0
      DO 670 NE=1,NPTSE
      TEL(NE)=EMINEL+FLOAT(NE-1)*DEE
      TEE(NE)=EXP(TEL(NE))
      CALL BSPOL (TEL(NE),MMAXE,EE,RE,REB,REC,RED,ANS)
      RINE(NE)=EXP(ANS)
      CALL BSPOL (TEL(NE),NMAXE,TE,RS,RSB,RSC,RSD,ANS)
      RINS(NE)=RINE(NE)*EXP(ANS)
      CALL BSPOL (TEL(NE),NMAXE,TE,AR,ARB,ARC,ARD,ANS)
      ARES(NE)=EXP(ANS)
      CALL BSPOL (TEL(NE),MMAXE,EE,YE,YEB,YEC,YED,ANS)
  670 YLDE(NE)=EXP(ANS)
      DO 820 M=1,2
      DO 815 NE=1,NPTSE
      DO 700 L=1,LMAXS
      IF (TEL(NE).LT.TE(NMAXE)) GO TO 680
      DIN(L)=DALE(NMAXE,L)
      GO TO 700
  680 IF (TEL(NE).GT.TE(NBEGE)) GO TO 690
      DIN(L)=DALE(NBEGE,L)
      GO TO 700
  690 CALL BSPOL (TEL(NE),NLENE,TE(NBEGE),DALE(NBEGE,L),DALEB(NBEGE,L),
     1   DALEC(NBEGE,L),DALED(NBEGE,L),DIN(L))
  700 CONTINUE
      DO 715 L=1,LMAXE
      DRIN(L)=1.0
      IF (IDET.EQ.1.AND.M.EQ.1) GO TO 715
      IF (TEL(NE).LT.TE(NMAXE)) GO TO 710
      DRIN(L)=DRATE(NMAXE,L,M)
      GO TO 715
  710 IF (TEL(NE).GT.TE(NBEGE)) GO TO 712
      DRIN(L)=DRATE(NBEGE,L,M)
      GO TO 715
  712 CALL BSPOL (TEL(NE),NLENE,TE(NBEGE),DRATE(NBEGE,L,M),
     1   DRATEB(NBEGE,L,M),DRATEC(NBEGE,L,M),DRATED(NBEGE,L,M),DRIN(L))
      IF (DRIN(L).LT.0.0) DRIN(L)=0.0
  715 CONTINUE
C     CALL LCOF (LMAXS,BS,DIN,DINB,DINC,DIND)
      CALL SCOF (LMAXS,BS,DIN,DINB,DINC,DIND)
      DO 740 I=1,IMAX
      ZRIN=Z(I)/RINS(NE)
      IF (ZRIN.LT.BS(LMAXS)) GO TO 730
  720 GE(NE,I,M)=0.0
      GO TO 740
  730 CALL BSPOL (ZRIN,LMAXS,BS,DIN,DINB,DINC,DIND,ANS)
      ANS=EXP(ANS)
      GE(NE,I,M)=TEE(NE)*ANS*ARES(NE)/RINS(NE)
  740 CONTINUE
C     CALL LCOF (LMAXE,ZRE,DRIN,DINB,DINC,DIND)
      CALL SCOF (LMAXE,ZRE,DRIN,DINB,DINC,DIND)
      DO 745 I=1,IMAX
      ZRIN=Z(I)/RINE(NE)
      IF (ZRIN.LT.ZRE(LMAXE)) GO TO 742
      GE(NE,I,M)=GE(NE,I,M)*DRIN(LMAXE)
      GO TO 745
  742 CALL BSPOL (ZRIN,LMAXE,ZRE,DRIN,DINB,DINC,DIND,ANSR)
      IF (ANSR.LT.0.0) ANSR=0.0
      GE(NE,I,M)=GE(NE,I,M)*ANSR
  745 CONTINUE
      DO 780 L=1,LMAXT
      IF (TEL(NE).LT.TE(NMAXE)) GO TO 760
      DIN(L)=DALB(NMAXE,L)
      GO TO 780
  760 IF (TEL(NE).GT.TE(NBEGE)) GO TO 770
      DIN(L)=DALB(NBEGE,L)
      GO TO 780
  770 CALL BSPOL (TEL(NE),NLENE,TE(NBEGE),DALB(NBEGE,L),DALBB(NBEGE,L),
     1   DALBC(NBEGE,L),DALBD(NBEGE,L),DIN(L))
  780 CONTINUE
      DO 795 L=1,LMAXB
      DRIN(L)=1.0
      IF (IDET.EQ.1.AND.M.EQ.1) GO TO 795
      IF (TEL(NE).LT.TE(NMAXE)) GO TO 790
      DRIN(L)=DRATB(NMAXE,L,M)
      GO TO 795
  790 IF (TEL(NE).GT.TE(NBEGE)) GO TO 792
      DRIN(L)=DRATB(NBEGE,L,M)
      GO TO 795
  792 CALL BSPOL (TEL(NE),NLENE,TE(NBEGE),DRATB(NBEGE,L,M),
     1   DRATBB(NBEGE,L,M),DRATBC(NBEGE,L,M),DRATBD(NBEGE,L,M),DRIN(L))
      IF (DRIN(L).LT.0.0) DRIN(L)=0.0
  795 CONTINUE
      CALL LCOF (LMAXT,ZS,DIN,DINB,DINC,DIND)
C     CALL SCOF (LMAXT,ZS,DIN,DINB,DINC,DIND)
      DO 800 I=1,IMAX
      ZRINL=LOG(Z(I)/RINE(NE))
      CALL BSPOL (ZRINL,LMAXT,ZS,DIN,DINB,DINC,DIND,ANS)
      ANS=EXP(ANS)
      GB(NE,I,M)=TEE(NE)*ANS*YLDE(NE)/RINE(NE)
  800 CONTINUE
      CALL LCOF (LMAXB,ZB,DRIN,DINB,DINC,DIND)
C     CALL SCOF (LMAXB,ZB,DRIN,DINB,DINC,DIND)
      DO 812 I=1,IMAX
      IF (ZL(I).LT.ZB(LMAXB)) GO TO 810
      GB(NE,I,M)=GB(NE,I,M)*DRIN(LMAXB)
      GO TO 812
  810 CALL BSPOL (ZL(I),LMAXB,ZB,DRIN,DINB,DINC,DIND,ANSR)
      IF (ANSR.LT.0.0) ANSR=0.0
      GB(NE,I,M)=GB(NE,I,M)*ANSR
  812 CONTINUE
  815 CONTINUE
  820 CONTINUE
      PRINT *,' Performing calculations for input spectra..............'
  830 WRITE (10,840)
  840 FORMAT (/)
      WRITE (10,850)
  850 FORMAT (' ')
      READ (9,20,END=1440) TAG
      PRINT 860, TAG
  860 FORMAT (4X,A)
      WRITE (10,20) TAG
      WRITE (12,20) TAG
      WRITE (10,870)
  870 FORMAT (/' JSMAX JPMAX JEMAX       EUNIT      DURATN')
      READ (9,*)  JSMAX,JPMAX,JEMAX,EUNIT,DURATN
      WRITE (10,880) JSMAX,JPMAX,JEMAX,EUNIT,DURATN
      WRITE (12,880) JSMAX,JPMAX,JEMAX,EUNIT,DURATN
  880 FORMAT (3I6,1P2E12.5)
      IF (DURATN.LE.0.0) DURATN=1.0
      DELTAS=RADCON*DELP/4.0
      DELTAP=DURATN*RADCON*DELP/4.0
      DELTAE=DURATN*RADCON*DELE/4.0
      IF (EUNIT.LE.0.0) EUNIT=1.0
      ISOL=2
      IF (JSMAX.LT.3) GO TO 900
      ISOL=1
      WRITE (10,885)
  885 FORMAT (//' E(MeV)')
      READ (9,*) (EPS(J),J=1,JSMAX)
      WRITE (10,905) (EPS(J),J=1,JSMAX)
      WRITE (12,905) (EPS(J),J=1,JSMAX)
      WRITE (10,890)
  890 FORMAT (/' SOLAR PROTON SPECTRUM (/energy/cm2)')
      READ (9,*) (S(J),J=1,JSMAX)
      WRITE (10,905) (S(J),J=1,JSMAX)
      WRITE (12,905) (S(J),J=1,JSMAX)
      NLENS=NLENSB
      NFSTS=NFSTSB
      NLSTS=NLSTSB
      CALL SPECTR (JSMAX,EPS,S,EUNIT,EMINU,DEP,NPTSP,NFSTS,NLSTS,NLENS,
     1   TPP,TPL,SOL)
      WRITE (10,891) TPP(NFSTS),TPP(NLSTS),NLENS
  891 FORMAT (/' SPECTRUM INTEGRATED FROM',1PE11.4,' TO',1PE11.4,
     1   ' MeV, USING',I5,' POINTS')
      DO 892 NP=NFSTS,NLSTS
  892 G(NP)=SOL(NP)*ENEWT(NP)
      CALL INTEG (DELP,G(NFSTS),NLENS,ENEUT)
      DO 894 NP=NFSTS,NLSTS
  894 G(NP)=SOL(NP)*TPP(NP)
      CALL INTEG (DELP,G(NFSTS),NLENS,EAV)
      ENEUT=ENEUT/EAV
      WRITE (10,896) ENEUT
  896 FORMAT (/' ASSUMED FRACTION OF BEAM ENERGY INTO NEUTRON ENERGY =',
     1   1PE12.5)
  900 ITRP=2
      IF (JPMAX.LT.3) GO TO 920
      ITRP=1
      WRITE (10,885)
      READ (9,*) (EPS(J),J=1,JPMAX)
      WRITE (10,905) (EPS(J),J=1,JPMAX)
      WRITE (12,905) (EPS(J),J=1,JPMAX)
  905 FORMAT (1P10E12.4)
      WRITE (10,910)
  910 FORMAT (/' TRAPPED PROTON SPECTRUM (/energy/cm2/time)')
      READ (9,*) (S(J),J=1,JPMAX)
      WRITE (10,905) (S(J),J=1,JPMAX)
      WRITE (12,905) (S(J),J=1,JPMAX)
      NLENP=NLENPB
      NFSTP=NFSTPB
      NLSTP=NLSTPB
      CALL SPECTR (JPMAX,EPS,S,EUNIT,EMINU,DEP,NPTSP,NFSTP,NLSTP,NLENP,
     1   TPP,TPL,SPG)
      WRITE (10,891) TPP(NFSTP),TPP(NLSTP),NLENP
      DO 912 NP=NFSTP,NLSTP
  912 G(NP)=SPG(NP)*ENEWT(NP)
      CALL INTEG (DELP,G(NFSTP),NLENP,ENEUT)
      DO 914 NP=NFSTP,NLSTP
  914 G(NP)=SPG(NP)*TPP(NP)
      CALL INTEG (DELP,G(NFSTP),NLENP,EAV)
      ENEUT=ENEUT/EAV
      WRITE (10,896) ENEUT
  920 ILEC=2
      IF (JEMAX.LT.3) GO TO 940
      ILEC=1
      WRITE (10,885)
      READ (9,*) (EPS(J),J=1,JEMAX)
      WRITE (10,905) (EPS(J),J=1,JEMAX)
      WRITE (12,905) (EPS(J),J=1,JEMAX)
      WRITE (10,930)
  930 FORMAT (/' ELECTRON SPECTRUM (/energy/cm2/time)')
      READ (9,*) (S(J),J=1,JEMAX)
      WRITE (10,905) (S(J),J=1,JEMAX)
      WRITE (12,905) (S(J),J=1,JEMAX)
      NLENE=NPTSE
      NFSTE=1
      NLSTE=NPTSE
      CALL SPECTR (JEMAX,EPS,S,EUNIT,EMINE,DEE,NPTSE,NFSTE,NLSTE,NLENE,
     1   TEE,TEL,SEG)
      WRITE (10,891) TEE(NFSTE),TEE(NLSTE),NLENE
  940 GO TO (980,950), ISOL
  950 DO 960 NP=NFSTS,NLSTS
  960 SOL(NP)=0.0
      DO 970 J=1,2
      DO 970 I=1,IMAX
  970 DOSOL(I,J)=0.0
      GO TO 1010
  980 DO 1000 I=1,IMAX
      DO 990 NP=NFSTS,NLSTS
  990 G(NP)=SOL(NP)*GP(NP,I)
      CALL INTEG (DELTAS,G(NFSTS),NLENS,DOSOL(I,1))
 1000 CONTINUE
      CALL SPHERE (ZL,DOSOL(1,1),IMAX,DOSOL(1,2))
 1010 GO TO (1050,1020), ITRP
 1020 DO 1030 NP=NFSTP,NLSTP
 1030 SPG(NP)=0.0
      DO 1040 J=1,2
      DO 1040 I=1,IMAX
 1040 DOSP(I,J)=0.0
      GO TO 1080
 1050 DO 1070 I=1,IMAX
      DO 1060 NP=NFSTP,NLSTP
 1060 G(NP)=SPG(NP)*GP(NP,I)
      CALL INTEG (DELTAP,G(NFSTP),NLENP,DOSP(I,1))
 1070 CONTINUE
      CALL SPHERE (ZL,DOSP(1,1),IMAX,DOSP(1,2))
 1080 GO TO (1110,1090), ILEC
 1090 DO 1100 J=1,2
      DO 1100 M=1,2
      DO 1100 I=1,IMAX
      DOSE(I,M,J)=0.0
 1100 DOSB(I,M,J)=0.0
      GO TO 1160
 1110 DO 1150 M=1,2
      DO 1130 I=1,IMAX
      DO 1120 NE=NFSTE,NLSTE
      G(NE)=SEG(NE)*GE(NE,I,M)
 1120 SPG(NE)=SEG(NE)*GB(NE,I,M)
      CALL INTEG (DELTAE,G(NFSTE),NLENE,DOSE(I,M,1))
      CALL INTEG (DELTAE,SPG(NFSTE),NLENE,DOSB(I,M,1))
 1130 CONTINUE
      GO TO (1140,1150), M
 1140 CALL SPHERE (ZL,DOSE(1,M,1),IMAX,DOSE(1,M,2))
      CALL SPHERE (ZL,DOSB(1,M,1),IMAX,DOSB(1,M,2))
 1150 CONTINUE
 1160 J=1
      DO 1340 M=2,1,-1
      GO TO (1190,1170), M
 1170 WRITE (10,1180)
 1180 FORMAT(//' DOSE AT TRANSMISSION SURFACE OF FINITE ALUMINUM SLAB SH
     1IELDS')
      GO TO 1210
 1190 WRITE (10,1200)
 1200 FORMAT(//' DOSE IN SEMI-INFINITE ALUMINUM MEDIUM')
 1210 WRITE (10,1230) DET(IDET)
 1230 FORMAT (/' rads ',A)
      IF (INATT.EQ.1) WRITE (10,1240)
 1240 FORMAT (/' Proton results without nuclear attenuation')
      IF (INATT.EQ.2) WRITE (10,1250)
 1250 FORMAT (/' Proton results with approximate treatment of nuclear at
     1tenuation')
      IF (INATT.EQ.2.AND.INEWT.EQ.0) WRITE (10,1260)
 1260 FORMAT ( '    neglecting transport of energy by neutrons')
      IF (INATT.EQ.2.AND.INEWT.EQ.1) WRITE (10,1270)
 1270 FORMAT ( '    and crude exponential transport of energy by neutron
     1s')
      WRITE (10,1310)
 1310 FORMAT(/'    Z(mils)      Z(mm)   Z(g/cm2)   ELECTRON    BREMS    
     1  EL+BR     TRP PROT   SOL PROT  EL+BR+TRP    TOTAL')
      WRITE (10,850)
      DO 1330 I=1,IMAX
      DOSEB=DOSE(I,M,J)+DOSB(I,M,J)
      DOSEBP=DOSEB+DOSP(I,J)
      DOST=DOSEBP+DOSOL(I,J)
      WRITE (10,1320) ZM(I),ZMM(I),Z(I),DOSE(I,M,J),DOSB(I,M,J),DOSEB,
     1   DOSP(I,J),DOSOL(I,J),DOSEBP,DOST
 1320 FORMAT (1P10E11.3)
      IF (FLOAT(I/10).EQ.0.1*FLOAT(I)) WRITE (10,850)
 1330 CONTINUE
 1340 CONTINUE
      J=2
      M=1
      WRITE (10,1350)
 1350 FORMAT (//' 1/2 DOSE AT CENTER OF ALUMINUM SPHERES')
      WRITE (10,1230) DET(IDET)
      IF (INATT.EQ.1) WRITE (10,1240)
      IF (INATT.EQ.2) WRITE (10,1250)
      IF (INATT.EQ.2.AND.INEWT.EQ.0) WRITE (10,1260)
      IF (INATT.EQ.2.AND.INEWT.EQ.1) WRITE (10,1270)
      WRITE (10,1310)
      WRITE (10,850)
      DO 1410 I=1,IMAX
      DOSEB=DOSE(I,M,J)+DOSB(I,M,J)
      DOSEBP=DOSEB+DOSP(I,J)
      DOST=DOSEBP+DOSOL(I,J)
      WRITE (10,1320) ZM(I),ZMM(I),Z(I),DOSE(I,M,J),DOSB(I,M,J),DOSEB,
     1   DOSP(I,J),DOSOL(I,J),DOSEBP,DOST
      IF (FLOAT(I/10).EQ.0.1*FLOAT(I)) WRITE (10,850)
 1410 CONTINUE
      DO 1437 J=1,2
      WRITE (12,1435) (DOSOL(I,J),I=1,IMAX)
      WRITE (12,1435) (DOSP(I,J),I=1,IMAX)
 1435 FORMAT (1P10E10.3)
 1437 CONTINUE
      DO 1438 M=2,1,-1
      WRITE (12,1435) (DOSE(I,M,1),I=1,IMAX)
      WRITE (12,1435) (DOSB(I,M,1),I=1,IMAX)
 1438 CONTINUE
      WRITE (12,1435) (DOSE(I,1,2),I=1,IMAX)
      WRITE (12,1435) (DOSB(I,1,2),I=1,IMAX)
      GO TO 830
 1440 PRINT 32, FILENM
      PRINT 34, PRTFIL
      PRINT 36, ARRFIL
C      print 1500,CHAR(27)
C 1500 format (' ',A1,'[0m')
      STOP
      END
      
C     SUBROUTINE EINDEX (EMINB,DE,NPTS,EMIN,EMAX,NFST,NLST,NLEN), 28 APR 94.
      SUBROUTINE EINDEX (EMINB,DE,NPTS,EMIN,EMAX,NFST,NLST,NLEN)
C     IMPLICIT DOUBLE PRECISION (A-H,O-Z)
      NFST=LOG(EMIN/EMINB)/DE+0.5
      NFST=NFST+1
      IF (NFST.LT.1) NFST=1
      NLST=LOG(EMAX/EMINB)/DE+0.5
      NLST=NLST+1
      IF (NLST.GT.NPTS) NLST=NPTS
      NLEN=NLST-NFST+1
      RETURN
      END
C     SUBROUTINE SPECTR (JMAX,EPS,S,EUNIT,EMINB,DEL,NPTS,NFST,NLST,NLEN,
C    1   T,TL,SP), 28 APR 94.
      SUBROUTINE SPECTR (JMAX,EPS,S,EUNIT,EMINB,DEL,NPTS,NFST,NLST,NLEN,
     1   T,TL,SP)
C     IMPLICIT DOUBLE PRECISION (A-H,O-Z)
      PARAMETER  (JMAXI=301,NPTSI=1001)
      DIMENSION  EPS(1),S(1),T(1),TL(1),SP(1),BCOF(JMAXI),CCOF(JMAXI),
     1   DCOF(JMAXI),G(NPTSI)
C           ARGLIM = 700.0 (DOUBLE PRECISION), 85.0 (SINGLE PRECISION)
      DATA  EMC2/938.27231/,ARGLIM/85.0/
      DELTA=DEL/3.0
      IF (EPS(1).GT.0.0) GO TO 20
      ALPHA=S(1)
      BETA=S(2)
      IF (BETA.LE.0.0) BETA=1.0
      BETA=BETA/ALPHA
      DO 10 N=NFST,NLST
      SP(N)=0.0
      G(N)=0.0
      IF (S(3).LE.0.0) GO TO 6
      P=SQRT(T(N)*(T(N)+2.0*EMC2))
      ARG=P/ALPHA
      IF (ARG.GT.ARGLIM) GO TO 10
      SP(N)=T(N)*BETA*((T(N)+EMC2)/P)*EXP(-ARG)
      GO TO 8
    6 ARG=T(N)/ALPHA
      IF (ARG.GT.ARGLIM) GO TO 10
      SP(N)=T(N)*BETA*EXP(-ARG)
    8 G(N)=T(N)*SP(N)
   10 CONTINUE
      GO TO 50
   20 CALL EINDEX (EMINB,DEL,NPTS,EPS(1),EPS(JMAX),NFST,NLST,NLEN)
      DO 30 J=1,JMAX
      EPS(J)=LOG(EPS(J))
   30 S(J)=LOG(EUNIT*S(J))
      CALL SCOF (JMAX,EPS,S,BCOF,CCOF,DCOF)
      DO 40 N=NFST,NLST
      CALL BSPOL (TL(N),JMAX,EPS,S,BCOF,CCOF,DCOF,ANS)
      SP(N)=T(N)*EXP(ANS)
   40 G(N)=T(N)*SP(N)
   50 CALL INTEG (DELTA,SP(NFST),NLEN,SIN)
      CALL INTEG (DELTA,G(NFST),NLEN,EBAR)
      EBAR=EBAR/SIN
      WRITE (10,60)
   60 FORMAT (/'    INT SPEC    EAV(MeV)')
      WRITE (10,70) SIN,EBAR
   70 FORMAT (1PE12.4,0PF12.5)
      RETURN
      END
C     SUBROUTINE SPHERE (ZL,DOSE,IMAX,DOSPH), 26 JAN 93.
      SUBROUTINE SPHERE (ZL,DOSE,IMAX,DOSPH)
C     IMPLICIT DOUBLE PRECISION (A-H,O-Z)
      PARAMETER  (IMAXI=71)
      DIMENSION  ZL(1),DOSE(1),DOSPH(1),DOSL(IMAXI),BCOF(IMAXI),
     1   CCOF(IMAXI),DCOF(IMAXI)
      DO 10 I=1,IMAX
      IF (DOSE(I).LE.0.0) GO TO 20
   10 DOSL(I)=LOG(DOSE(I))
      I=IMAX+1
   20 IMIX=I-1
      IF (IMIX.LT.3) GO TO 40
      CALL SCOF (IMIX,ZL,DOSL,BCOF,CCOF,DCOF)
      BCOF(IMIX)=BCOF(IMIX-1)+(2.0*CCOF(IMIX-1)+3.0*DCOF(IMIX-1)*
     1   (ZL(IMIX)-ZL(IMIX-1)))*(ZL(IMIX)-ZL(IMIX-1))
      DO 30 I=1,IMIX
   30 DOSPH(I)=DOSE(I)*(1.0-BCOF(I))
   40 IMIX1=IMIX+1
      IF (IMIX1.GT.IMAX) RETURN
      DO 50 I=IMIX1,IMAX
   50 DOSPH(I)=0.0
      RETURN
      END
      SUBROUTINE SCOF(N,X,Y,B,C,D) 
C        REINSCH ALGORITHM, VIA MJB, 22 FEB 83
C        Y(S)=((D(J)*(X-X(J))+C(J))*(X-X(J))+B(J))*(X-X(J))+Y(J) 
C             FOR X BETWEEN X(J) AND X(J+1)
C     IMPLICIT DOUBLE PRECISION (A-H,O-Z)
      DIMENSION X(1),Y(1),B(1),C(1),D(1) 
      N1=N-1 
      S=0.0
      DO 10 J=1,N1
      D(J)=X(J+1)-X(J)
      R=(Y(J+1)-Y(J))/D(J)
      C(J)=R-S
   10 S=R 
      S=0.0
      R=0.0
      C(1)=0.0
      C(N)=0.0
      DO 20 J=2,N1 
      C(J)=C(J)+R*C(J-1)
      B(J)=(X(J-1)-X(J+1))*2.0-R*S
      S=D(J)
   20 R=S/B(J)
      DO 30 JR=N1,2,-1
   30 C(JR)=(D(JR)*C(JR+1)-C(JR))/B(JR) 
      DO 40 J=1,N1
      S=D(J)
      R=C(J+1)-C(J) 
      D(J)=R/S
      C(J)=3.0*C(J)
   40 B(J)=(Y(J+1)-Y(J))/S-(C(J)+R)*S 
      RETURN
      END 
      SUBROUTINE BSPOL(S,N,X,Y,B,C,D,T)
C        BINARY SEARCH, X ASCENDING OR DESCENDING
C     IMPLICIT DOUBLE PRECISION (A-H,O-Z)
      DIMENSION X(1),Y(1),B(1),C(1),D(1)
      IF (X(1).GT.X(N)) GO TO 10
      IDIR=0
      MLB=0 
      MUB=N 
      GO TO 20
   10 IDIR=1
      MLB=N 
      MUB=0 
   20 IDIR1=IDIR-1
      IF (S.GE.X(MUB+IDIR)) GO TO 60
      IF (S.LE.X(MLB-IDIR1)) GO TO 70
      ML=MLB
      MU=MUB
      GO TO 40
   30 IF (IABS(MU-ML).LE.1) GO TO 80
   40 MAV=(ML+MU)/2 
      IF (S.LT.X(MAV)) GO TO 50 
      ML=MAV
      GO TO 30
   50 MU=MAV
      GO TO 30
   60 MU=MUB+IDIR+IDIR1 
      GO TO 90
   70 MU=MLB-IDIR-IDIR1 
      GO TO 90
   80 MU=MU+IDIR1
   90 Q=S-X(MU) 
      T=((D(MU)*Q+C(MU))*Q+B(MU))*Q+Y(MU) 
      RETURN
      END 
      SUBROUTINE LCOF (NMAX,X,F,B,C,D)
C        26 JAN 93.  SIMPLE LINEAR INTERPOLATION
C     IMPLICIT DOUBLE PRECISION (A-H,O-Z)
      DIMENSION  X(1), F(1), B(1), C(1), D(1)
      DO 10 N=1,NMAX-1
      B(N)=(F(N+1)-F(N))/(X(N+1)-X(N))
      C(N)=0.0
      D(N)=0.0
   10 CONTINUE
      RETURN
      END
      SUBROUTINE INTEG (DELTA,G,N,RESULT)
C          INCLUDES N=1
C     IMPLICIT DOUBLE PRECISION (A-H,O-Z)
      DIMENSION G(8)
      NL1=N-1
      NL2=N-2
      IF (REAL (N) -2.0*REAL (N/2)) 100,100,10
   10  IF (N-1) 15,15,20
   15 SIGMA=0.0
      GO TO 70
   20 IF(N-3) 30,30,40
   30 SIGMA=G(1)+4.0*G(2)+G(3)
      GO TO 70
   40 SUM4=0.0
      DO 50 K=2,NL1,2
   50 SUM4=SUM4+G(K)
      SUM2=0.0
      DO 60 K=3,NL2,2
   60 SUM2=SUM2+G(K)
      SIGMA=G(1)+4.0*SUM4+2.0*SUM2+G(N)
   70 RESULT=DELTA*SIGMA
      RETURN
  100 IF(N-2)110,110,120
  110 SIGMA=1.5*(G(1)+G(2))
      GO TO 70
  120 IF(N-4)130,130,140
  130 SIGMA=1.125*(G(1)+3.0*G(2)+3.0*G(3)+G(4))
      GO TO 70
  140 IF(N-6)150,150,160
  150 SIGMA=G(1)+3.875*G(2)+2.625*G(3)+2.625*G(4)+3.875*G(5)+G(6)
      GO TO 70
  160 IF (N-8)170,170,180
  170 SIGMA=G(1)+3.875*G(2)+2.625*G(3)+2.625*G(4)+3.875*G(5)+2.0*G(6)
     1   +4.0*G(7)+G(8)
      GO TO 70
  180 SIG6=G(1)+3.875*G(2)+2.625*G(3)+2.625*G(4)+3.875*G(5)+G(6)
      SUM4=0.0
      DO 190 K=7,NL1,2
  190 SUM4=SUM4+G(K)
      SUM2=0.0
      DO 200 K=8,NL2,2
  200 SUM2=SUM2+G(K)
      SIGMA=SIG6+G(6)+4.0*SUM4+2.0*SUM2+G(N)
      GO TO 70
      END
      SUBROUTINE LOGO (VERSION)
C        28PR 94.
      CHARACTER  VERSION*4,blue*10,green*7
      CALL CLS
      blue=CHAR(27)//'[40;36;1m'
      print 5,blue
    5 format (1X,A)
      PRINT 10,' '
   10 FORMAT (a80)
      PRINT 20
   20 FORMAT (6X,'�',66('�'),'�',6(' '))
      PRINT 25
   25 FORMAT (6X,'�',66X,'�',6(' '))
      PRINT 30
   30 FORMAT (6X,'�',27X,'SHIELDOSE-2',28X,'�',6(' '))
      PRINT 25
      PRINT 40
   40 FORMAT (6X,'�',15X,'A Computer Code for Space-Shielding',16X,'�',
     1   6(' '))
      PRINT 50
   50 FORMAT (6X,'�',19X,'Radiation Dose Calculations',20X,'�',6(' '))
      PRINT 25
      PRINT 60, VERSION
   60 FORMAT (6X,'�',27X,'Version ',A,27X,'�',6(' '))
      PRINT 25
      PRINT 70
   70 FORMAT (6X,'�',28X,'Written by',28X,'�',6(' '))
      PRINT 80
   80 FORMAT (6X,'�',24X,'STEPHEN M. SELTZER',24x,'�',6(' '))
      PRINT 90
   90 FORMAT (6X,'�',10X,'National Institute of Standards and Technology
     1',10x,'�',6(' '))
      PRINT 100
  100 FORMAT (6X,'�',20X,'Gaithersburg, MD 20899, USA',19X,'�',6(' '))
      PRINT 25
      PRINT 110
  110 FORMAT (6X,'�',66('�'),'�',6(' '))
      green=CHAR(27)//'[0;32m'
      print 5,green
      RETURN
      END
      SUBROUTINE CLS
C        8 SEP 88.
      PRINT 10,CHAR(27)
   10 FORMAT (' ',A1,'[2J')
      RETURN
      END