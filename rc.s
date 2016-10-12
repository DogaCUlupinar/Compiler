/*
 * Generated Tue Oct 11 23:14:15 PDT 2016
 */

.section ".rodata"
 _endl:	.asciz  "\n"
_intFmt:	.asciz  "%d"
_strFmt:	.asciz  "%s"
_boolT:	.asciz  "true"
_boolF:	.asciz  "false"
_arrErr:	.asciz  "Index value of %d is outside legal range [0,%d)\n"
_ptrErr:	.asciz  "Attempt to dereference NULL pointer.\n"
_ec1Err:	.asciz  "Attempt to dereference a pointer into deallocated stack space.\n"
.global  lowestSP
.section ".data"
 .align  4
lowestSP:	.word  0

.global  c
.section ".data"
 .align  4
c:	.word  10

.section ".text"
 .global  foo1
.align  4
foo1:
set	SAVE.foo1, %g1
save	%sp, %g1, %sp

set	120, %l1
set	-4, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]


set	-4, %l0
add	%fp, %l0, %l0
ld	[%l0], %o0

set	c, %l0
add	%g0, %l0, %l0
ld	[%l0], %o1

call	.mul
nop

set	-8, %l0
add	%fp, %l0, %l0
st	%o0, [%l0]

	set	-8, %l0
	add	%fp, %l0, %l0
	ld	[%l0], %o1
	
	set	_intFmt, %o0
	call	printf
	nop
	
	set	_endl, %o0
	call	printf
	nop
	
set	-4, %l0
add	%fp, %l0, %l0
ld	[%l0], %o0

set	c, %l0
add	%g0, %l0, %l0
ld	[%l0], %o1

call	.div
nop

set	-12, %l0
add	%fp, %l0, %l0
st	%o0, [%l0]

	set	-12, %l0
	add	%fp, %l0, %l0
	ld	[%l0], %o1
	
	set	_intFmt, %o0
	call	printf
	nop
	
	set	_endl, %o0
	call	printf
	nop
	
set	-4, %l0
add	%fp, %l0, %l0
ld	[%l0], %o0

set	c, %l0
add	%g0, %l0, %l0
ld	[%l0], %o1

call	.rem
nop

set	-16, %l0
add	%fp, %l0, %l0
st	%o0, [%l0]

	set	-16, %l0
	add	%fp, %l0, %l0
	ld	[%l0], %o1
	
	set	_intFmt, %o0
	call	printf
	nop
	
	set	_endl, %o0
	call	printf
	nop
	
set	2, %l1
set	2, %o0
set	c, %l0
add	%g0, %l0, %l0
ld	[%l0], %o1

call	.mul
nop

set	-20, %l0
add	%fp, %l0, %l0
st	%o0, [%l0]

set	-4, %l0
add	%fp, %l0, %l0
ld	[%l0], %l1

set	-20, %l0
add	%fp, %l0, %l0
ld	[%l0], %l2

add	%l1, %l2, %l1
set	-24, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]

set	2, %l1
set	-4, %l0
add	%fp, %l0, %l0
ld	[%l0], %o0

set	2, %o1
call	.div
nop

set	-28, %l0
add	%fp, %l0, %l0
st	%o0, [%l0]

set	-24, %l0
add	%fp, %l0, %l0
ld	[%l0], %l1

set	-28, %l0
add	%fp, %l0, %l0
ld	[%l0], %l2

sub	%l1, %l2, %l1
set	-32, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]

set	6, %l1
set	c, %l0
add	%g0, %l0, %l0
ld	[%l0], %o0

set	6, %o1
call	.rem
nop

set	-36, %l0
add	%fp, %l0, %l0
st	%o0, [%l0]

set	-32, %l0
add	%fp, %l0, %l0
ld	[%l0], %l1

set	-36, %l0
add	%fp, %l0, %l0
ld	[%l0], %l2

add	%l1, %l2, %l1
set	-40, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]

set	-4, %l0
add	%fp, %l0, %l0
ld	[%l0], %l1

neg	%l1
set	-44, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]

set	-44, %l0
add	%fp, %l0, %l0
ld	[%l0], %o0

set	c, %l0
add	%g0, %l0, %l0
ld	[%l0], %o1

call	.mul
nop

set	-48, %l0
add	%fp, %l0, %l0
st	%o0, [%l0]

set	-40, %l0
add	%fp, %l0, %l0
ld	[%l0], %l1

set	-48, %l0
add	%fp, %l0, %l0
ld	[%l0], %l2

add	%l1, %l2, %l1
set	-52, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]

set	-52, %l0
add	%fp, %l0, %l0
ld	[%l0], %i0

ret
restore

		set	lowestSP, %l1
		ld	[%l1], %l0
		
		cmp	%l0, 0
		bne	elseECOne1
		nop
		
		mov	%sp, %l0
		ba	endECOne1
		nop
		
	elseECOne1:
		
		cmp	%sp, %l0
		bgeu	endECOne1
		nop
		
		mov	%sp, %l0
	
	endECOne1:
		
		st	%l0, [%l1]
		
	foo1XoX:
	
	SAVE.foo1 = -(92 + 52) & -8
.section ".text"
 .global  foo2
.align  4
foo2:
set	SAVE.foo2, %g1
save	%sp, %g1, %sp

set	120, %l1
set	-60, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]


set	-60, %l0
add	%fp, %l0, %l0
ld	[%l0], %l1

set	-60, %l0
add	%fp, %l0, %l0
ld	[%l0], %l2

or	%l1, %l2, %l3
set	-64, %l0
add	%fp, %l0, %l0
st	%l3, [%l0]


	set	-64, %l0
	add	%fp, %l0, %l0
	ld	[%l0], %o1
	
	set	_intFmt, %o0
	call	printf
	nop
	
	set	_endl, %o0
	call	printf
	nop
	
set	-60, %l0
add	%fp, %l0, %l0
ld	[%l0], %l1

set	-60, %l0
add	%fp, %l0, %l0
ld	[%l0], %l2

xor	%l1, %l2, %l3
set	-68, %l0
add	%fp, %l0, %l0
st	%l3, [%l0]


	set	-68, %l0
	add	%fp, %l0, %l0
	ld	[%l0], %o1
	
	set	_intFmt, %o0
	call	printf
	nop
	
	set	_endl, %o0
	call	printf
	nop
	
set	0, %l1
set	-60, %l0
add	%fp, %l0, %l0
ld	[%l0], %l1

set	0, %l2
and	%l1, %l2, %l3
set	-72, %l0
add	%fp, %l0, %l0
st	%l3, [%l0]


	set	-72, %l0
	add	%fp, %l0, %l0
	ld	[%l0], %o1
	
	set	_intFmt, %o0
	call	printf
	nop
	
	set	_endl, %o0
	call	printf
	nop
	
set	1, %l1
mov	%l1, %i0
ret
restore

		set	lowestSP, %l1
		ld	[%l1], %l0
		
		cmp	%l0, 0
		bne	elseECOne2
		nop
		
		mov	%sp, %l0
		ba	endECOne2
		nop
		
	elseECOne2:
		
		cmp	%sp, %l0
		bgeu	endECOne2
		nop
		
		mov	%sp, %l0
	
	endECOne2:
		
		st	%l0, [%l1]
		
	foo2XoX:
	
	SAVE.foo2 = -(92 + 72) & -8
.section ".text"
 .global  foo3
.align  4
foo3:
set	SAVE.foo3, %g1
save	%sp, %g1, %sp

set	1, %l1
set	-80, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]


set	2, %l1
set	-84, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]


set	3, %l1
set	-88, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]


set	4, %l1
set	-92, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]


set	5, %l1
set	-96, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]


set	6, %l1
set	-100, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]


set	7, %l1
set	-104, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]


set	8, %l1
set	-108, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]


set	-80, %l0
add	%fp, %l0, %l0
ld	[%l0], %l1

set	-84, %l0
add	%fp, %l0, %l0
ld	[%l0], %l2

add	%l1, %l2, %l1
set	-112, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]

set	-88, %l0
add	%fp, %l0, %l0
ld	[%l0], %l1

set	-92, %l0
add	%fp, %l0, %l0
ld	[%l0], %l2

add	%l1, %l2, %l1
set	-116, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]

set	-112, %l0
add	%fp, %l0, %l0
ld	[%l0], %l1

set	-116, %l0
add	%fp, %l0, %l0
ld	[%l0], %l2

add	%l1, %l2, %l1
set	-120, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]

set	-96, %l0
add	%fp, %l0, %l0
ld	[%l0], %l1

set	-100, %l0
add	%fp, %l0, %l0
ld	[%l0], %l2

add	%l1, %l2, %l1
set	-124, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]

set	-120, %l0
add	%fp, %l0, %l0
ld	[%l0], %l1

set	-124, %l0
add	%fp, %l0, %l0
ld	[%l0], %l2

add	%l1, %l2, %l1
set	-128, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]

set	-104, %l0
add	%fp, %l0, %l0
ld	[%l0], %l1

set	-108, %l0
add	%fp, %l0, %l0
ld	[%l0], %l2

add	%l1, %l2, %l1
set	-132, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]

set	-128, %l0
add	%fp, %l0, %l0
ld	[%l0], %l1

set	-132, %l0
add	%fp, %l0, %l0
ld	[%l0], %l2

add	%l1, %l2, %l1
set	-136, %l0
add	%fp, %l0, %l0
st	%l1, [%l0]

set	-136, %l0
add	%fp, %l0, %l0
ld	[%l0], %i0

ret
restore

		set	lowestSP, %l1
		ld	[%l1], %l0
		
		cmp	%l0, 0
		bne	elseECOne3
		nop
		
		mov	%sp, %l0
		ba	endECOne3
		nop
		
	elseECOne3:
		
		cmp	%sp, %l0
		bgeu	endECOne3
		nop
		
		mov	%sp, %l0
	
	endECOne3:
		
		st	%l0, [%l1]
		
	foo3XoX:
	
	SAVE.foo3 = -(92 + 136) & -8
.section ".text"
 .global  main
.align  4
main:
set	SAVE.main, %g1
save	%sp, %g1, %sp

	.section ".data"
 	.align  4
	tmp1:		.asciz  "foo1"
	.section ".text"
 	.align  4
	
	set	tmp1, %o1
	set	_strFmt, %o0
	call	printf
	nop
	
	set	_endl, %o0
	call	printf
	nop
	
call	foo1
nop

	mov	%o0, %o1
	set	_intFmt, %o0
	call	printf
	nop
	
	set	_endl, %o0
	call	printf
	nop
	
	.section ".data"
 	.align  4
	tmp2:		.asciz  "foo2"
	.section ".text"
 	.align  4
	
	set	tmp2, %o1
	set	_strFmt, %o0
	call	printf
	nop
	
	set	_endl, %o0
	call	printf
	nop
	
call	foo2
nop

	mov	%o0, %o1
	set	_intFmt, %o0
	call	printf
	nop
	
	set	_endl, %o0
	call	printf
	nop
	
	.section ".data"
 	.align  4
	tmp3:		.asciz  "foo3"
	.section ".text"
 	.align  4
	
	set	tmp3, %o1
	set	_strFmt, %o0
	call	printf
	nop
	
	set	_endl, %o0
	call	printf
	nop
	
call	foo3
nop

	mov	%o0, %o1
	set	_intFmt, %o0
	call	printf
	nop
	
	set	_endl, %o0
	call	printf
	nop
	
		set	lowestSP, %l1
		ld	[%l1], %l0
		
		cmp	%l0, 0
		bne	elseECOne4
		nop
		
		mov	%sp, %l0
		ba	endECOne4
		nop
		
	elseECOne4:
		
		cmp	%sp, %l0
		bgeu	endECOne4
		nop
		
		mov	%sp, %l0
	
	endECOne4:
		
		st	%l0, [%l1]
		
	mainXoX:
	ret
	restore
	
	SAVE.main = -(92 + 140) & -8
