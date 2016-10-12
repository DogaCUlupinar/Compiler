/*
 * Generated Fri Feb 14 14:55:52 PST 2014
 */

.section ".rodata"
 _endl:	.asciz  "\n"
_intFmt:	.asciz  "%d"
_strFmt:	.asciz  "%s"
_boolT:	.asciz  "true"
_boolF:	.asciz  "false"

.section ".text"
 .global  main
.align  4
main:
set	SAVE.main, %g1
save	%sp, %g1, %sp

.section ".data"
 .align  4
tmp1:	.single  0r3.2
.section ".text"
 .align  4

set	tmp1, %l0
add	%fp, %l0, %l0
ld	[%l0], %l1

set	-4, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]


	set	-4, %l0
	add	%fp, %l0, %l0
	ld	[%l0], %f0
	
	call	printFloat
	nop
	
	set	_endl, %o0
	call	printf
	nop
	
	ret
	restore
	
	SAVE.main = -(92 + 4) & -8
