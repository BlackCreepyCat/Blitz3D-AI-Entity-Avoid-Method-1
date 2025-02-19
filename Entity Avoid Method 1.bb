
Global gameFPS = 50

Graphics3D 1024,768,32,2
SetBuffer BackBuffer()

SeedRnd MilliSecs()

Global camera = CreateCamera ()
CameraZoom camera,1.6
CameraRange camera,.1,100000
CameraClsColor camera,0,0,0
PositionEntity camera,0,140,-800

AmbientLight 150,150,150

light = CreateLight()
PositionEntity light,0,0,0

quit = False

framePeriod = 1000 / gameFPS
frameTime = MilliSecs () - framePeriod

Type cube
	Field mesh
	Field walktimer
	Field turntimer
	Field moving
	Field decision
	Field distfromhome
	Field id
End Type

For i = 1 To 10
	c.cube = New cube
	c\id = i
	c\mesh = CreateCube()
	EntityColor c\mesh,Rand(0,255),Rand(0,255),Rand(0,255)
	ScaleEntity c\mesh,10,10,10
	PositionEntity c\mesh,0,10,0
	d.cube = c.cube
Next

home = CreateSphere()
ScaleEntity home,6,6,6
PositionEntity home,0,10,0

PointEntity camera,home

Plane=CreatePlane()
EntityAlpha Plane,0.8
PlaneTexture=CreateTexture(128,128,9)
ClsColor 0,0,255
Cls
Color 255,255,255
Rect 0,0,64,64,1
Rect 64,64,64,64,1
CopyRect 0,0,128,128,0,0,BackBuffer(),TextureBuffer(PlaneTexture)
ScaleTexture PlaneTexture,40,40
EntityTexture Plane,PlaneTexture,0,0

Repeat

	Repeat
		frameElapsed = MilliSecs () - frameTime
	Until frameElapsed

	frameTicks = frameElapsed / framePeriod

	frameTween# = Float (frameElapsed Mod framePeriod) / Float (framePeriod)

	For frameLimit = 1 To frameTicks

		If frameLimit = frameTicks Then CaptureWorld
		frameTime = frameTime + framePeriod
		
		If KeyHit(1) Then quit = True
		
		For c.cube = Each cube
		
			For d.cube = Each cube
				If c\id <> d\id And c\moving = True
					avoid_entity( c\mesh, d\mesh, 2, 2 )
				EndIf
			Next
			
			c\distfromhome = EntityDistance(c\mesh,home)
				
			If c\moving = False
				c\decision = Rand(1,200)
				Select True
					Case c\decision = 10
						c\walktimer = MilliSecs() + 3000
					Case c\decision = 100
						c\turntimer = MilliSecs() + 1000
				End Select
			EndIf
	
			If c\walktimer > MilliSecs() 
				c\moving = True
				MoveEntity c\mesh,0,0,2
			Else If c\turntimer > MilliSecs()
				c\moving = True
				TurnEntity c\mesh,0,2,0
			Else
				c\moving = False
			EndIf
	
			If c\distfromhome > 400
				dy#=DeltaYaw(c\mesh,home)*.04
				TurnEntity c\mesh,0,dy,0
			EndIf
			
		Next

		UpdateWorld

	Next
	
	RenderWorld frameTween
	
	Flip

Until quit = True

End

Function avoid_entity( scr_entity, dest_entity, rate#, spd# )

	If EntityDistance(scr_entity,dest_entity) < 100 Then
		MoveEntity scr_entity,0,0,spd#
		dist1# = EntityDistance(scr_entity,dest_entity)
		MoveEntity scr_entity,0,0,-spd#
		TurnEntity scr_entity,0,rate#,0
		MoveEntity scr_entity,0,0,spd#
		dist2# = EntityDistance(scr_entity,dest_entity)
		MoveEntity scr_entity,0,0,-spd#
		TurnEntity scr_entity,0,-2*rate#,0
		MoveEntity scr_entity,0,0,spd#
		dist3# = EntityDistance(scr_entity,dest_entity)
		MoveEntity scr_entity,0,0,-spd#
		
		If dist1# > dist2# And dist1# > dist3# Then TurnEntity scr_entity,0,rate#,0
		If dist2# > dist3# And dist2# > dist1# Then TurnEntity scr_entity,0,2*rate#,0
		
		Return True
	Else
		Return False
	EndIf

End Function
