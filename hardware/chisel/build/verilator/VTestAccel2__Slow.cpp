// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Design implementation internals
// See VTestAccel2.h for the primary calling header

#include "VTestAccel2.h"       // For This
#include "VTestAccel2__Syms.h"

#include "verilated_dpi.h"

//--------------------
// STATIC VARIABLES


//--------------------

VL_CTOR_IMP(VTestAccel2) {
    VTestAccel2__Syms* __restrict vlSymsp = __VlSymsp = new VTestAccel2__Syms(this, name());
    VTestAccel2* __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Reset internal values
    
    // Reset structure values
    _ctor_var_reset();
}

void VTestAccel2::__Vconfigure(VTestAccel2__Syms* vlSymsp, bool first) {
    if (0 && first) {}  // Prevent unused
    this->__VlSymsp = vlSymsp;
}

VTestAccel2::~VTestAccel2() {
    delete __VlSymsp; __VlSymsp=NULL;
}

//--------------------
// Internal Methods

void VTestAccel2::_initial__TOP__1(VTestAccel2__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::_initial__TOP__1\n"); );
    VTestAccel2* __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
    // INITIAL at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1684
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vxrand1 
	= VL_RAND_RESET_Q(64);
}

void VTestAccel2::_initial__TOP__4(VTestAccel2__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::_initial__TOP__4\n"); );
    VTestAccel2* __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
    // INITIAL at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1728
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0 
	= (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
	   | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[1U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[2U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[3U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[4U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[5U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[6U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[7U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[8U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[9U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0xaU] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0xbU] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0xcU] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0xdU] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0xeU] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0xfU] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x10U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x11U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x12U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x13U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x14U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x15U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x16U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x17U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x18U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x19U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x1aU] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x1bU] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x1cU] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x1dU] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x1eU] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x1fU] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x20U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x21U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x22U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x23U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x24U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x25U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x26U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[0x27U] 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_1 
	= (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
	   | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_2 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value 
	= (0x3fU & vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_2);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_3 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value_1 
	= (0x3fU & vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_3);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_4 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT__maybe_full 
	= (1U & vlTOPp->TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_4);
    // INITIAL at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1510
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_0 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rstate 
	= (3U & vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_0);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_1 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate 
	= (3U & vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_1);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_2 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_cnt 
	= (0xfU & vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_2);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_3 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rd_len 
	= (0xfU & vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_3);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_4 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_len 
	= (0xfU & vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_4);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_5 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rd_addr 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_5;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_6 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_addr 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_6;
    // INITIAL at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:486
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___RAND_0 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__opcode 
	= (1U & vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___RAND_0);
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___RAND_1 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__len 
	= (0xffU & vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___RAND_1);
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___RAND_2 
	= (((QData)((IData)(VL_RANDOM_I(32))) << 0x20U) 
	   | (QData)((IData)(VL_RANDOM_I(32))));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__addr 
	= vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___RAND_2;
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___RAND_3 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state 
	= (7U & vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___RAND_3);
    // INITIAL at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:2096
    vlTOPp->TestAccel2__DOT__vta_shell__DOT___RAND_0 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__Rstate 
	= (3U & vlTOPp->TestAccel2__DOT__vta_shell__DOT___RAND_0);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT___RAND_1 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__Wstate 
	= (3U & vlTOPp->TestAccel2__DOT__vta_shell__DOT___RAND_1);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT___RAND_2 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__value 
	= (0xffU & vlTOPp->TestAccel2__DOT__vta_shell__DOT___RAND_2);
    // INITIAL at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:1102
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_0 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__waddr 
	= (0xffffU & vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_0);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_1 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__wstate 
	= (3U & vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_1);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_2 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rstate 
	= (1U & vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_2);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_3 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rdata 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_3;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_4 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_0 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_4;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_5 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_1 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_5;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_6 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_2 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_6;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_7 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_3 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_7;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_8 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_4 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_8;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_9 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_5 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_9;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_10 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_6 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_10;
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_11 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_7 
	= vlTOPp->TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_11;
    // INITIAL at /localhome/rshojabro/git/dpi-playground/hardware/chisel/build/chisel/TestAccel2.v:131
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___RAND_0 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__addr 
	= (0xffU & vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___RAND_0);
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___RAND_1 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__data 
	= vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___RAND_1;
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___RAND_2 
	= VL_RANDOM_I(32);
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state 
	= (7U & vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___RAND_2);
}

void VTestAccel2::_settle__TOP__5(VTestAccel2__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::_settle__TOP__5\n"); );
    VTestAccel2* __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
    vlTOPp->sim_wait = vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT__wait_reg;
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
}

void VTestAccel2::_settle__TOP__7(VTestAccel2__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::_settle__TOP__7\n"); );
    VTestAccel2* __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
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
}

void VTestAccel2::_settle__TOP__9(VTestAccel2__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::_settle__TOP__9\n"); );
    VTestAccel2* __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
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
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_96 
	= ((IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_93) 
	   & (0U == (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__len)));
    vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_108 
	= ((IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_93) 
	   & (0U != (IData)(vlTOPp->TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__len)));
}

void VTestAccel2::_eval_initial(VTestAccel2__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::_eval_initial\n"); );
    VTestAccel2* __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
    vlTOPp->_initial__TOP__1(vlSymsp);
    vlTOPp->_initial__TOP__4(vlSymsp);
}

void VTestAccel2::final() {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::final\n"); );
    // Variables
    VTestAccel2__Syms* __restrict vlSymsp = this->__VlSymsp;
    VTestAccel2* __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
}

void VTestAccel2::_eval_settle(VTestAccel2__Syms* __restrict vlSymsp) {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::_eval_settle\n"); );
    VTestAccel2* __restrict vlTOPp VL_ATTR_UNUSED = vlSymsp->TOPp;
    // Body
    vlTOPp->_settle__TOP__5(vlSymsp);
    vlTOPp->_settle__TOP__7(vlSymsp);
    vlTOPp->_settle__TOP__9(vlSymsp);
}

void VTestAccel2::_ctor_var_reset() {
    VL_DEBUG_IF(VL_PRINTF("    VTestAccel2::_ctor_var_reset\n"); );
    // Body
    clock = VL_RAND_RESET_I(1);
    reset = VL_RAND_RESET_I(1);
    sim_clock = VL_RAND_RESET_I(1);
    sim_wait = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT_____05Freset = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT_____05Fwait = VL_RAND_RESET_I(8);
    TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT_____05Fexit = VL_RAND_RESET_I(8);
    TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT__wait_reg = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi_dpi_req_valid = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi_dpi_req_opcode = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi_dpi_req_addr = VL_RAND_RESET_I(8);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi_dpi_req_value = VL_RAND_RESET_I(32);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freset = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_valid = VL_RAND_RESET_I(8);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_opcode = VL_RAND_RESET_I(8);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_addr = VL_RAND_RESET_I(8);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_value = VL_RAND_RESET_I(32);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__addr = VL_RAND_RESET_I(8);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___RAND_0 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__data = VL_RAND_RESET_I(32);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___RAND_1 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state = VL_RAND_RESET_I(3);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___RAND_2 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_72 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_73 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_74 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_75 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_76 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_77 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_79 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi_dpi_rd_valid = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi_dpi_rd_bits = VL_RAND_RESET_Q(64);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi_io_axi_r_valid = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT_____05Freset = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT_____05Frd_valid = VL_RAND_RESET_I(8);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT_____05Frd_value = VL_RAND_RESET_Q(64);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__opcode = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___RAND_0 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__len = VL_RAND_RESET_I(8);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___RAND_1 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__addr = VL_RAND_RESET_Q(64);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___RAND_2 = VL_RAND_RESET_Q(64);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state = VL_RAND_RESET_I(3);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___RAND_3 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_90 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_91 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_92 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_93 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_96 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_97 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_98 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_99 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_100 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_108 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_112 = VL_RAND_RESET_I(8);
    TestAccel2__DOT__vta_shell__DOT__vcr_io_vcr_finish = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vcr_io_vcr_ecnt_0_valid = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vmem_io_mem_w_valid = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vmem_io_mem_r_ready = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vmem_io_vme_wr_0_ack = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__Rstate = VL_RAND_RESET_I(2);
    TestAccel2__DOT__vta_shell__DOT___RAND_0 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__Wstate = VL_RAND_RESET_I(2);
    TestAccel2__DOT__vta_shell__DOT___RAND_1 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__value = VL_RAND_RESET_I(8);
    TestAccel2__DOT__vta_shell__DOT___RAND_2 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT___T_101 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT___T_104 = VL_RAND_RESET_I(8);
    TestAccel2__DOT__vta_shell__DOT___T_109 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT___T_111 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT___T_112 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT___T_113 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT___T_114 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT___T_115 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT__waddr = VL_RAND_RESET_I(16);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_0 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT__wstate = VL_RAND_RESET_I(2);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_1 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rstate = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_2 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rdata = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_3 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_0 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_4 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_1 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_5 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_2 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_6 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_3 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_7 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_4 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_8 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_5 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_9 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_6 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_10 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_7 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_11 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_135 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_136 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_137 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_138 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_143 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_149 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_152 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_156 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_160 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_164 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_168 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_172 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_176 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_180 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_181 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_188 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rstate = VL_RAND_RESET_I(2);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_0 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_191 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_192 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_193 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_195 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate = VL_RAND_RESET_I(2);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_1 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_cnt = VL_RAND_RESET_I(4);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_2 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_201 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_204 = VL_RAND_RESET_I(4);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_205 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_206 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_207 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_210 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_211 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rd_len = VL_RAND_RESET_I(4);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_3 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_len = VL_RAND_RESET_I(4);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_4 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rd_addr = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_5 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_addr = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_6 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_221 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vxrand1 = VL_RAND_RESET_Q(64);
    { int __Vi0=0; for (; __Vi0<40; ++__Vi0) {
	    TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[__Vi0] = VL_RAND_RESET_Q(64);
    }}
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0 = VL_RAND_RESET_Q(64);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram___05FT_65_data = VL_RAND_RESET_Q(64);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_1 = VL_RAND_RESET_Q(64);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value = VL_RAND_RESET_I(6);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_2 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value_1 = VL_RAND_RESET_I(6);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_3 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT__maybe_full = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_4 = VL_RAND_RESET_I(32);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_41 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT__empty = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_44 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT__do_enq = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT__do_deq = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT__wrap = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_53 = VL_RAND_RESET_I(6);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT__wrap_1 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_58 = VL_RAND_RESET_I(6);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_60 = VL_RAND_RESET_I(1);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2 = VL_RAND_RESET_Q(64);
    TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound3 = VL_RAND_RESET_Q(64);
    __Vclklast__TOP__clock = VL_RAND_RESET_I(1);
    __Vclklast__TOP__sim_clock = VL_RAND_RESET_I(1);
}
