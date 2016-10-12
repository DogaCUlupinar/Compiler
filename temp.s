/*
 * Generated Tue Feb 25 16:48:07 PST 2014
 */

.section ".rodata"
 _endl:	.asciz  "\n"
_intFmt:	.asciz  "%d"
_strFmt:	.asciz  "%s"
_boolT:	.asciz  "true"
_boolF:	.asciz  "false"

.global  gi
.section ".data"
 .align  4
gi:

.word  0
.word  0
.word  0
.global  gf
.section ".data"
 .align  4
gf:

.single  0r0
.single  0r0
.single  0r0
.global  gb
.section ".data"
 .align  4
gb:

.word  0
.word  0
.word  0
.section ".text"
 .global  main
.align  4
main:
set	SAVE.main, %g1
save	%sp, %g1, %sp

set	-12, %l0
add	%l0, 4, %l0
add	%fp, %l0, %l0
ld	[%l0], %l6

	set	2, %l1
	set	-12, %l0
	add	%l0, 4, %l0
	add	%fp, %l0, %l0
	st	%l1, [%l0]
	
set	-24, %l0
add	%l0, 8, %l0
add	%fp, %l0, %l0
ld	[%l0], %f0

	.section ".data"
 	.align  4
	tmp1:		.single  0r1.8
	.section ".text"
 	.align  4
	
	set	tmp1, %l0
	add	%g0, %l0, %l0
	ld	[%l0], %f0
	
	set	-24, %l0
	add	%l0, 8, %l0
	add	%fp, %l0, %l0
	st	%f0, [%l0]
	
THE LINE YOU ARE INTERESTED IN IS HERE !!!
set	-24, %l0
add	%l0, 8, %l0
add	%fp, %l0, %l0
ld	[%l0], %f0

set	-12, %l0
add	%l0, 4, %l0
add	%fp, %l0, %l0
ld	[%l0], %l6

sll	%l6, 2, %l5
set	-12, %l0
add	%l0, %l5, %l0
add	%fp, %l0, %l0
ld	[%l0], %l6

	set	-12, %l0
	sll	%l6, 2, %l5
	add	%l0, %l5, %l0
	add	%fp, %l0, %l0
	ld	[%l0], %l1
	
	set	-12, %l0
	add	%l0, %l5, %l0
	add	%fp, %l0, %l0
	ld	[%l0], %l1
	
		set	-24, %l0
		add	%l0, 8, %l0
		add	%fp, %l0, %l0
		st	%f0, [%l0]
		
		ret
		restore
		
		SAVE.main = -(92 + 36) & -8
