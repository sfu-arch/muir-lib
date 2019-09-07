// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design implementation internals
// See VTestAccel2.h for the primary calling header

#include "VTestAccel2.h"       // For This
#include "VTestAccel2__Syms.h"

#include "verilated_dpi.h"

//--------------------


void VTestAccel2::eval() {
    VTestAccel2__Syms* __restrict vlSymsp = this->__VlSymsp; // Setup global symbol table
    VTestAccel2* __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Initialize
    if (VL_UNLIKELY(!vlSymsp->__Vm_didInit)) _eval_initial_loop(vlSymsp);
    // Evaluate till stable
    VL_DEBUG_IF(VL_PRINTF("\n----TOP Evaluate VTestAccel2::eval\n"); );
    int __VclockLoop = 0;
    QData __Vchange=1;
    while (VL_LIKELY(__Vchange)) {
	VL_DEBUG_IF(VL_PRINTF(" Clock loop\n"););
	vlSymsp->__Vm_activity = true;
	_eval(vlSymsp);
	__Vchange = _change_request(vlSymsp);
	if (++__VclockLoop > 100) vl_fatal(__FILE__,__LINE__,__FILE__,"Verilated model didn't converge");
    }
}

void VTestAccel2::_eval_initial_loop(VTestAccel2__Syms* __restrict vlSymsp) {
    vlSymsp->__Vm_didInit = true;
    _eval_initial(vlSymsp);
    vlSymsp->__Vm_activity = true;
    int __VclockLoop = 0;
    QData __Vchange=1;
    while (VL_LIKELY(__Vchange)) {
	_eval_settle(vlSymsp);
	_eval(vlSymsp);
	__Vchange = _change_request(vlSymsp);
	if (++__VclockLoop > 100) vl_fatal(__FILE__,__LINE__,__FILE__,"Verilated model didn't DC converge");
    }
}

//--------------------
// Internal Methods

VL_INLINE_OPT void VTestAccel2::__Vdpiimwrap_TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT__VTASimDPI_TOP(CData& sim_wait, CData& sim_exit) {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::__Vdpiimwrap_TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT__VTASimDPI_TOP\n"); );
    // Body
    unsigned char sim_wait__Vcvt;
    unsigned char sim_exit__Vcvt;
    VTASimDPI(&sim_wait__Vcvt, &sim_exit__Vcvt);
    sim_wait = (0xffU & sim_wait__Vcvt);
    sim_exit = (0xffU & sim_exit__Vcvt);
}

VL_INLINE_OPT void VTestAccel2::__Vdpiimwrap_TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT__VTAHostDPI_TOP(CData& req_valid, CData& req_opcode, CData& req_addr, IData& req_value, CData req_deq, CData resp_valid, IData resp_value) {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::__Vdpiimwrap_TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT__VTAHostDPI_TOP\n"); );
    // Body
    unsigned char req_valid__Vcvt;
    unsigned char req_opcode__Vcvt;
    unsigned char req_addr__Vcvt;
    unsigned int req_value__Vcvt;
    unsigned char req_deq__Vcvt;
    req_deq__Vcvt = req_deq;
    unsigned char resp_valid__Vcvt;
    resp_valid__Vcvt = resp_valid;
    unsigned int resp_value__Vcvt;
    resp_value__Vcvt = resp_value;
    VTAHostDPI(&req_valid__Vcvt, &req_opcode__Vcvt, &req_addr__Vcvt, &req_value__Vcvt, req_deq__Vcvt, resp_valid__Vcvt, resp_value__Vcvt);
    req_valid = (0xffU & req_valid__Vcvt);
    req_opcode = (0xffU & req_opcode__Vcvt);
    req_addr = (0xffU & req_addr__Vcvt);
    req_value = req_value__Vcvt;
}

VL_INLINE_OPT void VTestAccel2::__Vdpiimwrap_TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT__VTAMemDPI_TOP(CData req_valid, CData req_opcode, CData req_len, QData req_addr, CData wr_valid, QData wr_value, CData& rd_valid, QData& rd_value, CData rd_ready) {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::__Vdpiimwrap_TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT__VTAMemDPI_TOP\n"); );
    // Body
    unsigned char req_valid__Vcvt;
    req_valid__Vcvt = req_valid;
    unsigned char req_opcode__Vcvt;
    req_opcode__Vcvt = req_opcode;
    unsigned char req_len__Vcvt;
    req_len__Vcvt = req_len;
    unsigned long long req_addr__Vcvt;
    req_addr__Vcvt = req_addr;
    unsigned char wr_valid__Vcvt;
    wr_valid__Vcvt = wr_valid;
    unsigned long long wr_value__Vcvt;
    wr_value__Vcvt = wr_value;
    unsigned char rd_valid__Vcvt;
    unsigned long long rd_value__Vcvt;
    unsigned char rd_ready__Vcvt;
    rd_ready__Vcvt = rd_ready;
    VTAMemDPI(req_valid__Vcvt, req_opcode__Vcvt, req_len__Vcvt, req_addr__Vcvt, wr_valid__Vcvt, wr_value__Vcvt, &rd_valid__Vcvt, &rd_value__Vcvt, rd_ready__Vcvt);
    rd_valid = (0xffU & rd_valid__Vcvt);
    rd_value = rd_value__Vcvt;
}

VL_INLINE_OPT void VTestAccel2::_sequent__TOP__2(VTestAccel2__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::_sequent__TOP__2\n"); );
    VTestAccel2* __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Variables
    VL_SIG8(__Vtask_TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT__VTAHostDPI__1__req_valid,7,0);
    VL_SIG8(__Vtask_TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT__VTAHostDPI__1__req_opcode,7,0);
    VL_SIG8(__Vtask_TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT__VTAHostDPI__1__req_addr,7,0);
    VL_SIG8(__Vtask_TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT__VTAMemDPI__2__rd_valid,7,0);
    VL_SIG8(__Vdly__TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state,2,0);
    VL_SIG8(__Vdly__TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state,2,0);
    VL_SIG8(__Vdly__TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rstate,0,0);
    VL_SIG8(__Vdlyvdim0__TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram__v0,5,0);
    VL_SIG8(__Vdlyvset__TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram__v0,0,0);
    //char	__VpadToAlign73[3];
    VL_SIG(__Vtask_TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT__VTAHostDPI__1__req_value,31,0);
    VL_SIG64(__Vtask_TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT__VTAMemDPI__2__rd_value,63,0);
    VL_SIG64(__Vdlyvval__TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram__v0,63,0);
    // Body
    __Vdly__TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rstate 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rstate;
    __Vdly__TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state 
	= vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state;
    __Vdly__TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state 
	= vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state;
    __Vdlyvset__TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram__v0 = 0U;
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/VTAHostDPI.v:79
    if (((IData)(vlTOPp->reset) | (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freset))) {
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_valid = 0U;
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_opcode = 0U;
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_addr = 0U;
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_value = 0U;
    } else {
	// Function: VTAHostDPI at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/VTAHostDPI.v:87
	vlTOPp->__Vdpiimwrap_TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT__VTAHostDPI_TOP(__Vtask_TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT__VTAHostDPI__1__req_valid, __Vtask_TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT__VTAHostDPI__1__req_opcode, __Vtask_TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT__VTAHostDPI__1__req_addr, __Vtask_TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT__VTAHostDPI__1__req_value, 
										(((1U 
										== (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state)) 
										& (~ (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rstate))) 
										| ((3U 
										== (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state)) 
										& (0U 
										== (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__wstate)))), (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rstate), vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rdata);
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_valid 
	    = __Vtask_TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT__VTAHostDPI__1__req_valid;
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_opcode 
	    = __Vtask_TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT__VTAHostDPI__1__req_opcode;
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_addr 
	    = __Vtask_TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT__VTAHostDPI__1__req_addr;
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_value 
	    = __Vtask_TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT__VTAHostDPI__1__req_value;
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/VTAMemDPI.v:88
    if (((IData)(vlTOPp->reset) | (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT_____05Freset))) {
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT_____05Frd_valid = 0U;
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT_____05Frd_value = VL_ULL(0);
    } else {
	// Function: VTAMemDPI at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/VTAMemDPI.v:94
	vlTOPp->__Vdpiimwrap_TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT__VTAMemDPI_TOP(
										(((1U 
										== (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state)) 
										& (1U 
										== (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rstate))) 
										| ((3U 
										== (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state)) 
										& (1U 
										== (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate)))), (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__opcode), vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__len, vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__addr, 
										((4U 
										== (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state)) 
										& (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem_io_mem_w_valid)), vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram___05FT_65_data, __Vtask_TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT__VTAMemDPI__2__rd_valid, __Vtask_TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT__VTAMemDPI__2__rd_value, 
										((2U 
										== (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state)) 
										& (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem_io_mem_r_ready)));
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT_____05Frd_valid 
	    = __Vtask_TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT__VTAMemDPI__2__rd_valid;
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT_____05Frd_value 
	    = __Vtask_TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT__VTAMemDPI__2__rd_value;
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/VTAMemDPI.v:75
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi_dpi_rd_valid 
	= (1U & (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT_____05Frd_valid));
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1160
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__waddr = 0xffffU;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_138) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__waddr 
		= vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__addr;
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1188
    if (vlTOPp->reset) {
	__Vdly__TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rstate = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_143) {
	    if ((1U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state))) {
		__Vdly__TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rstate = 1U;
	    }
	} else {
	    if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rstate) {
		if ((2U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state))) {
		    __Vdly__TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rstate = 0U;
		}
	    }
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1781
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__maybe_full = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_60) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__maybe_full 
		= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__do_enq;
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:167
    if (vlTOPp->reset) {
	__Vdly__TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_72) {
	    if (vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi_dpi_req_valid) {
		__Vdly__TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state 
		    = ((IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi_dpi_req_opcode)
		        ? 3U : 1U);
	    }
	} else {
	    if (vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_73) {
		if ((1U & (~ (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rstate)))) {
		    __Vdly__TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state = 2U;
		}
	    } else {
		if (vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_74) {
		    if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rstate) {
			__Vdly__TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state = 0U;
		    }
		} else {
		    if (vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_75) {
			if ((0U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__wstate))) {
			    __Vdly__TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state = 4U;
			}
		    } else {
			if (vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_76) {
			    if ((1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__wstate))) {
				__Vdly__TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state = 5U;
			    }
			} else {
			    if (vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_77) {
				if ((2U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__wstate))) {
				    __Vdly__TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state = 0U;
				}
			    }
			}
		    }
		}
	    }
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1596
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_cnt = 0U;
    } else {
	if ((0U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate))) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_cnt = 0U;
	} else {
	    if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_201) {
		vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_cnt 
		    = vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_204;
	    }
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1770
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value_1 = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__do_deq) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value_1 
		= ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__wrap_1)
		    ? 0U : (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_58));
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:557
    if (vlTOPp->reset) {
	__Vdly__TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_90) {
	    if ((1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rstate))) {
		__Vdly__TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state = 1U;
	    } else {
		if ((1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate))) {
		    __Vdly__TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state = 3U;
		}
	    }
	} else {
	    if (vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_91) {
		if ((1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rstate))) {
		    __Vdly__TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state = 2U;
		}
	    } else {
		if (vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_92) {
		    if (vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_96) {
			__Vdly__TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state = 0U;
		    }
		} else {
		    if (vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_97) {
			if ((1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate))) {
			    __Vdly__TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state = 4U;
			}
		    } else {
			if (vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_98) {
			    if (vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_99) {
				__Vdly__TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state = 5U;
			    }
			} else {
			    if (vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_100) {
				if ((3U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate))) {
				    __Vdly__TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state = 0U;
				}
			    }
			}
		    }
		}
	    }
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1756
    if (((~ (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_44)) 
	 & (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi_io_axi_r_valid))) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound3 
	    = (vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi_dpi_rd_bits 
	       + (QData)((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_2)));
	if ((0x27U >= (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value))) {
	    __Vdlyvval__TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram__v0 
		= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound3;
	    __Vdlyvset__TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram__v0 = 1U;
	    __Vdlyvdim0__TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram__v0 
		= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value;
	}
    }
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rstate 
	= __Vdly__TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rstate;
    // ALWAYSPOST at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1757
    if (__Vdlyvset__TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram__v0) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[__Vdlyvdim0__TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram__v0] 
	    = __Vdlyvval__TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram__v0;
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/VTAHostDPI.v:61
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freset 
	= vlTOPp->reset;
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1203
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rdata = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_181) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rdata 
		= ((0U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__addr))
		    ? vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_0
		    : ((4U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__addr))
		        ? vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_1
		        : ((8U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__addr))
			    ? vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_2
			    : ((0xcU == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__addr))
			        ? vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_3
			        : ((0x10U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__addr))
				    ? vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_4
				    : ((0x14U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__addr))
				        ? vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_5
				        : ((0x18U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__addr))
					    ? vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_6
					    : ((0x1cU 
						== (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__addr))
					        ? vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_7
					        : 0U))))))));
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/VTAMemDPI.v:68
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT_____05Freset 
	= vlTOPp->reset;
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:512
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__opcode = 0U;
    } else {
	if ((0U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state))) {
	    if ((1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rstate))) {
		vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__opcode = 0U;
	    } else {
		if ((1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate))) {
		    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__opcode = 1U;
		}
	    }
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:544
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__addr = VL_ULL(0);
    } else {
	if ((0U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state))) {
	    if ((1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rstate))) {
		vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__addr 
		    = (QData)((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rd_addr));
	    } else {
		if ((1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate))) {
		    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__addr 
			= (QData)((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_addr));
		}
	    }
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:525
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__len = 0U;
    } else {
	if ((0U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state))) {
	    if ((1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rstate))) {
		vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__len 
		    = vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rd_len;
	    } else {
		if ((1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate))) {
		    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__len 
			= vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_len;
		}
	    }
	} else {
	    if ((2U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state))) {
		if (vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_108) {
		    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__len 
			= vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_112;
		}
	    }
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/VTAHostDPI.v:69
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi_dpi_req_opcode 
	= (1U & (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_opcode));
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/VTAHostDPI.v:68
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi_dpi_req_valid 
	= (1U & (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_valid));
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1167
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__wstate = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_135) {
	    if ((3U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state))) {
		vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__wstate = 1U;
	    }
	} else {
	    if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_136) {
		if ((4U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state))) {
		    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__wstate = 2U;
		}
	    } else {
		if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_137) {
		    if ((5U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state))) {
			vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__wstate = 0U;
		    }
		}
	    }
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/VTAMemDPI.v:76
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi_dpi_rd_bits 
	= vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT_____05Frd_value;
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1759
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__do_enq) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value 
		= ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__wrap)
		    ? 0U : (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_53));
	}
    }
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state 
	= __Vdly__TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state;
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:153
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__addr = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_79) {
	    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__addr 
		= vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi_dpi_req_addr;
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1285
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_5 = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_172) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_5 
		= vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__data;
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1299
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_7 = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_180) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_7 
		= vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__data;
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1264
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_2 = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_160) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_2 
		= vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__data;
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1253
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_1 = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr_io_vcr_ecnt_0_valid) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_1 
		= vlTOPp->TestAccel2__DOT__vta_shell__DOT__value;
	} else {
	    if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_156) {
		vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_1 
		    = vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__data;
	    }
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1621
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rd_addr = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_188) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rd_addr 
		= vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_4;
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1628
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_addr = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_221) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_addr 
		= vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_6;
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1607
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rd_len = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_188) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rd_len 
		= (0xfU & vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_3);
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1614
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_len = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_221) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_len 
		= (0xfU & vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_3);
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1548
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rstate = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_191) {
	    if ((1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__Rstate))) {
		vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rstate = 1U;
	    }
	} else {
	    if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_192) {
		if ((1U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state))) {
		    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rstate = 2U;
		}
	    } else {
		if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_193) {
		    if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_195) {
			vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rstate = 0U;
		    }
		}
	    }
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1569
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_205) {
	    if ((1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__Wstate))) {
		vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate = 1U;
	    }
	} else {
	    if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_206) {
		if ((3U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state))) {
		    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate = 2U;
		}
	    } else {
		if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_207) {
		    if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_210) {
			vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate = 3U;
		    }
		} else {
		    if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_211) {
			if ((5U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state))) {
			    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate = 0U;
			}
		    }
		}
	    }
	}
    }
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state 
	= __Vdly__TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state;
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/VTAHostDPI.v:70
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi_dpi_req_addr 
	= vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_addr;
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:2156
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__value = 0U;
    } else {
	if ((0U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__Rstate))) {
	    if ((1U & vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_0)) {
		vlTOPp->TestAccel2__DOT__vta_shell__DOT__value = 0U;
	    } else {
		if ((0U != (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__Rstate))) {
		    vlTOPp->TestAccel2__DOT__vta_shell__DOT__value 
			= ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT___T_101)
			    ? 0U : (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT___T_104));
		}
	    }
	} else {
	    if ((0U != (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__Rstate))) {
		vlTOPp->TestAccel2__DOT__vta_shell__DOT__value 
		    = ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT___T_101)
		        ? 0U : (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT___T_104));
	    }
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1278
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_4 = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_168) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_4 
		= vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__data;
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1292
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_6 = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_176) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_6 
		= vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__data;
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1271
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_3 = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_164) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_3 
		= vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__data;
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:2137
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__Wstate = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem_io_vme_wr_0_ack) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__Wstate = 0U;
	} else {
	    if (vlTOPp->TestAccel2__DOT__vta_shell__DOT___T_113) {
		if ((1U & vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_0)) {
		    vlTOPp->TestAccel2__DOT__vta_shell__DOT__Wstate = 1U;
		}
	    } else {
		if (vlTOPp->TestAccel2__DOT__vta_shell__DOT___T_114) {
		    if (vlTOPp->TestAccel2__DOT__vta_shell__DOT___T_115) {
			vlTOPp->TestAccel2__DOT__vta_shell__DOT__Wstate = 2U;
		    }
		}
	    }
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:2118
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__Rstate = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem_io_vme_wr_0_ack) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__Rstate = 0U;
	} else {
	    if (vlTOPp->TestAccel2__DOT__vta_shell__DOT___T_109) {
		if ((1U & vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_0)) {
		    vlTOPp->TestAccel2__DOT__vta_shell__DOT__Rstate = 1U;
		}
	    } else {
		if (vlTOPp->TestAccel2__DOT__vta_shell__DOT___T_111) {
		    if (vlTOPp->TestAccel2__DOT__vta_shell__DOT___T_112) {
			vlTOPp->TestAccel2__DOT__vta_shell__DOT__Rstate = 2U;
		    }
		}
	    }
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1242
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_0 = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr_io_vcr_finish) {
	    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_0 = 2U;
	} else {
	    if (vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_152) {
		vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_0 
		    = vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__data;
	    }
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:160
    if (vlTOPp->reset) {
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__data = 0U;
    } else {
	if (vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_79) {
	    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__data 
		= vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi_dpi_req_value;
	}
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/VTAHostDPI.v:71
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi_dpi_req_value 
	= vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_value;
}

VL_INLINE_OPT void VTestAccel2::_sequent__TOP__3(VTestAccel2__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::_sequent__TOP__3\n"); );
    VTestAccel2* __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Variables
    VL_SIG8(__Vtask_TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT__VTASimDPI__0__sim_wait,7,0);
    VL_SIG8(__Vtask_TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT__VTASimDPI__0__sim_exit,7,0);
    //char	__VpadToAlign118[2];
    // Body
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/VTASimDPI.v:72
    if (VL_UNLIKELY((1U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT_____05Fexit)))) {
	vl_finish("/localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/VTASimDPI.v",74,"");
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/VTASimDPI.v:46
    if (((IData)(vlTOPp->reset) | (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT_____05Freset))) {
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT_____05Fwait = 0U;
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT_____05Fexit = 0U;
    } else {
	// Function: VTASimDPI at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/VTASimDPI.v:52
	vlTOPp->__Vdpiimwrap_TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT__VTASimDPI_TOP(__Vtask_TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT__VTASimDPI__0__sim_wait, __Vtask_TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT__VTASimDPI__0__sim_exit);
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT_____05Fwait 
	    = __Vtask_TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT__VTASimDPI__0__sim_wait;
	vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT_____05Fexit 
	    = __Vtask_TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT__VTASimDPI__0__sim_exit;
    }
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/VTASimDPI.v:60
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT__wait_reg 
	= ((~ ((IData)(vlTOPp->reset) | (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT_____05Freset))) 
	   & (1U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT_____05Fwait)));
    vlTOPp->sim_wait = vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT__wait_reg;
    // ALWAYS at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/VTASimDPI.v:41
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT_____05Freset 
	= vlTOPp->reset;
}

VL_INLINE_OPT void VTestAccel2::_sequent__TOP__6(VTestAccel2__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::_sequent__TOP__6\n"); );
    VTestAccel2* __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram___05FT_65_data 
	= ((0x28U <= (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value_1))
	    ? vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_1
	    : ((0x27U >= (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value_1))
	        ? vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram
	       [vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value_1]
	        : vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vxrand1));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__wrap_1 
	= (0x27U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value_1));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_58 
	= (0x3fU & ((IData)(1U) + (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value_1)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_41 
	= ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value) 
	   == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value_1));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__wrap 
	= (0x27U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_53 
	= (0x3fU & ((IData)(1U) + (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_204 
	= (0xfU & ((IData)(1U) + (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_cnt)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_191 
	= (0U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rstate));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_192 
	= (1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rstate));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_193 
	= (2U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rstate));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_205 
	= (0U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_206 
	= (1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_207 
	= (2U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_211 
	= (3U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_112 
	= (0xffU & ((IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__len) 
		    - (IData)(1U)));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_90 
	= (0U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_91 
	= (1U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_92 
	= (2U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_97 
	= (3U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_98 
	= (4U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_100 
	= (5U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi_io_axi_r_valid 
	= ((2U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state)) 
	   & (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi_dpi_rd_valid));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem_io_vme_wr_0_ack 
	= ((3U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate)) 
	   & (5U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT___T_101 
	= (0xc7U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__value));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT___T_104 
	= (0xffU & ((IData)(1U) + (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__value)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT___T_113 
	= (0U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__Wstate));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT___T_114 
	= (1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__Wstate));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT___T_115 
	= ((0U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate)) 
	   & (1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__Wstate)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_221 
	= ((0U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate)) 
	   & (1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__Wstate)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT___T_109 
	= (0U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__Rstate));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT___T_111 
	= (1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__Rstate));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT___T_112 
	= ((0U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rstate)) 
	   & (1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__Rstate)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_188 
	= ((0U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rstate)) 
	   & (1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__Rstate)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_143 
	= (1U & (~ (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rstate)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_135 
	= (0U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__wstate));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_136 
	= (1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__wstate));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_137 
	= (2U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__wstate));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_72 
	= (0U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_73 
	= (1U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_74 
	= (2U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_75 
	= (3U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_76 
	= (4U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_77 
	= (5U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_138 
	= ((0U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__wstate)) 
	   & (3U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_181 
	= ((~ (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rstate)) 
	   & (1U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_149 
	= ((1U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__wstate)) 
	   & (4U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state)));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_79 
	= ((0U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state)) 
	   & (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi_dpi_req_valid));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__empty 
	= ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_41) 
	   & (~ (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__maybe_full)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_44 
	= ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_41) 
	   & (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__maybe_full));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr_io_vcr_ecnt_0_valid 
	= ((2U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__Wstate)) 
	   & (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem_io_vme_wr_0_ack));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr_io_vcr_finish 
	= ((2U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__Wstate)) 
	   & (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem_io_vme_wr_0_ack));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_152 
	= ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_149) 
	   & (0U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__waddr)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_156 
	= ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_149) 
	   & (4U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__waddr)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_160 
	= ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_149) 
	   & (8U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__waddr)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_164 
	= ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_149) 
	   & (0xcU == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__waddr)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_168 
	= ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_149) 
	   & (0x10U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__waddr)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_172 
	= ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_149) 
	   & (0x14U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__waddr)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_176 
	= ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_149) 
	   & (0x18U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__waddr)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_180 
	= ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_149) 
	   & (0x1cU == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__waddr)));
}

VL_INLINE_OPT void VTestAccel2::_sequent__TOP__8(VTestAccel2__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::_sequent__TOP__8\n"); );
    VTestAccel2* __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__do_deq 
	= (((2U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate)) 
	    & (4U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state))) 
	   & (~ (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__empty)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem_io_mem_w_valid 
	= ((2U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate)) 
	   & (~ (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__empty)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_210 
	= (((~ (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__empty)) 
	    & (4U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state))) 
	   & ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_cnt) 
	      == (0xfU & vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_3)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__do_enq 
	= ((~ (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_44)) 
	   & (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi_io_axi_r_valid));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem_io_mem_r_ready 
	= ((2U == (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rstate)) 
	   & (~ (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_44)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_201 
	= ((4U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state)) 
	   & (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem_io_mem_w_valid));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_99 
	= ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem_io_mem_w_valid) 
	   & ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_cnt) 
	      == (0xfU & vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_3)));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_60 
	= ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__do_enq) 
	   != (IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__do_deq));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_195 
	= (((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem_io_mem_r_ready) 
	    & (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi_io_axi_r_valid)) 
	   & (0U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__len)));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_93 
	= ((IData)(vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem_io_mem_r_ready) 
	   & (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi_dpi_rd_valid));
}

VL_INLINE_OPT void VTestAccel2::_sequent__TOP__10(VTestAccel2__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::_sequent__TOP__10\n"); );
    VTestAccel2* __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_96 
	= ((IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_93) 
	   & (0U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__len)));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_108 
	= ((IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_93) 
	   & (0U != (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__len)));
}

void VTestAccel2::_eval(VTestAccel2__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::_eval\n"); );
    VTestAccel2* __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
    if (((IData)(vlTOPp->clock) & (~ (IData)(vlTOPp->__Vclklast__TOP__clock)))) {
	vlTOPp->_sequent__TOP__2(vlSymsp);
    }
    if (((IData)(vlTOPp->sim_clock) & (~ (IData)(vlTOPp->__Vclklast__TOP__sim_clock)))) {
	vlTOPp->_sequent__TOP__3(vlSymsp);
    }
    if (((IData)(vlTOPp->clock) & (~ (IData)(vlTOPp->__Vclklast__TOP__clock)))) {
	vlTOPp->_sequent__TOP__6(vlSymsp);
	vlTOPp->_sequent__TOP__8(vlSymsp);
	vlTOPp->_sequent__TOP__10(vlSymsp);
    }
    // Final
    vlTOPp->__Vclklast__TOP__clock = vlTOPp->clock;
    vlTOPp->__Vclklast__TOP__sim_clock = vlTOPp->sim_clock;
}

VL_INLINE_OPT QData VTestAccel2::_change_request(VTestAccel2__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::_change_request\n"); );
    VTestAccel2* __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
    return (vlTOPp->_change_request_1(vlSymsp));
}

VL_INLINE_OPT QData VTestAccel2::_change_request_1(VTestAccel2__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::_change_request_1\n"); );
    VTestAccel2* __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
    // Change detection
    QData __req = false;  // Logically a bool
    return __req;
}
