// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Primary design header
//
// This header should be included by all source files instantiating the design.
// The class here is then constructed to instantiate the design.
// See the Verilator manual for examples.

#ifndef _VTestAccel2_H_
#define _VTestAccel2_H_

#include "verilated.h"
#include "VTestAccel2__Dpi.h"

class VTestAccel2__Syms;

//----------

VL_MODULE(VTestAccel2) {
  public:
    // CELLS
    // Public to allow access to /*verilator_public*/ items;
    // otherwise the application code can consider these internals.
    
    // PORTS
    // The application code writes and reads these signals to
    // propagate new values into/out from the Verilated model.
    VL_IN8(clock,0,0);
    VL_IN8(sim_clock,0,0);
    VL_IN8(reset,0,0);
    VL_OUT8(sim_wait,0,0);
    
    // LOCAL SIGNALS
    // Internals; generally not touched by application code
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT_____05Freset,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT_____05Fwait,7,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT_____05Fexit,7,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT__wait_reg,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi_dpi_req_valid,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi_dpi_req_opcode,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi_dpi_req_addr,7,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freset,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_valid,7,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_opcode,7,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_addr,7,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__addr,7,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__state,2,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_72,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_73,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_74,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_75,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_76,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_77,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___T_79,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi_dpi_rd_valid,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi_io_axi_r_valid,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT_____05Freset,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT_____05Frd_valid,7,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__opcode,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__len,7,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__state,2,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_90,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_91,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_92,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_93,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_96,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_97,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_98,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_99,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_100,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_108,0,0);
    VL_SIG8(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___T_112,7,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vcr_io_vcr_finish,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vcr_io_vcr_ecnt_0_valid,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem_io_mem_w_valid,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem_io_mem_r_ready,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem_io_vme_wr_0_ack,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__Rstate,1,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__Wstate,1,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__value,7,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT___T_101,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT___T_104,7,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT___T_109,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT___T_111,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT___T_112,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT___T_113,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT___T_114,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT___T_115,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vcr__DOT__wstate,1,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rstate,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_135,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_136,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_137,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_138,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_143,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_149,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_152,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_156,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_160,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_164,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_168,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_172,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_176,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_180,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___T_181,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_188,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rstate,1,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_191,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_192,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_193,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_195,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wstate,1,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_cnt,3,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_201,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_204,3,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_205,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_206,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_207,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_210,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_211,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rd_len,3,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_len,3,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___T_221,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value,5,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__buffer__DOT__value_1,5,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__buffer__DOT__maybe_full,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_41,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__buffer__DOT__empty,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_44,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__buffer__DOT__do_enq,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__buffer__DOT__do_deq,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__buffer__DOT__wrap,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_53,5,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__buffer__DOT__wrap_1,0,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_58,5,0);
    VL_SIG8(TestAccel2__DOT__vta_shell__DOT__buffer__DOT___T_60,0,0);
    VL_SIG16(TestAccel2__DOT__vta_shell__DOT__vcr__DOT__waddr,15,0);
    VL_SIG(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi_dpi_req_value,31,0);
    VL_SIG(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT_____05Freq_value,31,0);
    VL_SIG(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___RAND_0,31,0);
    VL_SIG(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT__data,31,0);
    VL_SIG(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___RAND_1,31,0);
    VL_SIG(TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_axi__DOT___RAND_2,31,0);
    VL_SIG(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___RAND_0,31,0);
    VL_SIG(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___RAND_1,31,0);
    VL_SIG(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___RAND_3,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT___RAND_0,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT___RAND_1,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT___RAND_2,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_0,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_1,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_2,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT__rdata,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_3,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_0,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_4,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_1,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_5,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_2,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_6,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_3,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_7,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_4,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_8,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_5,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_9,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_6,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_10,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT__reg_7,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vcr__DOT___RAND_11,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_0,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_1,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_2,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_3,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_4,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vmem__DOT__rd_addr,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_5,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vmem__DOT__wr_addr,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__vmem__DOT___RAND_6,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_2,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_3,31,0);
    VL_SIG(TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_4,31,0);
    //char	__VpadToAlign292[4];
    VL_SIG64(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi_dpi_rd_bits,63,0);
    VL_SIG64(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT_____05Frd_value,63,0);
    VL_SIG64(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT__addr,63,0);
    VL_SIG64(TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_axi__DOT___RAND_2,63,0);
    VL_SIG64(TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_0,63,0);
    VL_SIG64(TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram___05FT_65_data,63,0);
    VL_SIG64(TestAccel2__DOT__vta_shell__DOT__buffer__DOT___RAND_1,63,0);
    VL_SIG64(TestAccel2__DOT__vta_shell__DOT__buffer__DOT__ram[40],63,0);
    
    // LOCAL VARIABLES
    // Internals; generally not touched by application code
    VL_SIG8(__Vclklast__TOP__clock,0,0);
    VL_SIG8(__Vclklast__TOP__sim_clock,0,0);
    //char	__VpadToAlign678[2];
    VL_SIG64(TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vxrand1,63,0);
    VL_SIG64(TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound2,63,0);
    VL_SIG64(TestAccel2__DOT__vta_shell__DOT__buffer__DOT____Vlvbound3,63,0);
    
    // INTERNAL VARIABLES
    // Internals; generally not touched by application code
    //char	__VpadToAlign708[4];
    VTestAccel2__Syms*	__VlSymsp;		// Symbol table
    
    // PARAMETERS
    // Parameters marked /*verilator public*/ for use by application code
    
    // CONSTRUCTORS
  private:
    VTestAccel2& operator= (const VTestAccel2&);	///< Copying not allowed
    VTestAccel2(const VTestAccel2&);	///< Copying not allowed
  public:
    /// Construct the model; called by application code
    /// The special name  may be used to make a wrapper with a
    /// single model invisible WRT DPI scope names.
    VTestAccel2(const char* name="TOP");
    /// Destroy the model; called (often implicitly) by application code
    ~VTestAccel2();
    
    // USER METHODS
    
    // API METHODS
    /// Evaluate the model.  Application must call when inputs change.
    void eval();
    /// Simulation complete, run final blocks.  Application must call on completion.
    void final();
    
    // INTERNAL METHODS
  private:
    static void _eval_initial_loop(VTestAccel2__Syms* __restrict vlSymsp);
  public:
    void __Vconfigure(VTestAccel2__Syms* symsp, bool first);
    void	__Vdpiimwrap_TestAccel2__DOT__sim_shell__DOT__mod_host__DOT__host_dpi__DOT__VTAHostDPI_TOP(CData& req_valid, CData& req_opcode, CData& req_addr, IData& req_value, CData req_deq, CData resp_valid, IData resp_value);
    void	__Vdpiimwrap_TestAccel2__DOT__sim_shell__DOT__mod_mem__DOT__mem_dpi__DOT__VTAMemDPI_TOP(CData req_valid, CData req_opcode, CData req_len, QData req_addr, CData wr_valid, QData wr_value, CData& rd_valid, QData& rd_value, CData rd_ready);
    void	__Vdpiimwrap_TestAccel2__DOT__sim_shell__DOT__mod_sim__DOT__sim__DOT__VTASimDPI_TOP(CData& sim_wait, CData& sim_exit);
  private:
    static QData	_change_request(VTestAccel2__Syms* __restrict vlSymsp);
    static QData	_change_request_1(VTestAccel2__Syms* __restrict vlSymsp);
    void	_ctor_var_reset();
  public:
    static void	_eval(VTestAccel2__Syms* __restrict vlSymsp);
    static void	_eval_initial(VTestAccel2__Syms* __restrict vlSymsp);
    static void	_eval_settle(VTestAccel2__Syms* __restrict vlSymsp);
    static void	_initial__TOP__1(VTestAccel2__Syms* __restrict vlSymsp);
    static void	_initial__TOP__4(VTestAccel2__Syms* __restrict vlSymsp);
    static void	_sequent__TOP__10(VTestAccel2__Syms* __restrict vlSymsp);
    static void	_sequent__TOP__2(VTestAccel2__Syms* __restrict vlSymsp);
    static void	_sequent__TOP__3(VTestAccel2__Syms* __restrict vlSymsp);
    static void	_sequent__TOP__6(VTestAccel2__Syms* __restrict vlSymsp);
    static void	_sequent__TOP__8(VTestAccel2__Syms* __restrict vlSymsp);
    static void	_settle__TOP__5(VTestAccel2__Syms* __restrict vlSymsp);
    static void	_settle__TOP__7(VTestAccel2__Syms* __restrict vlSymsp);
    static void	_settle__TOP__9(VTestAccel2__Syms* __restrict vlSymsp);
} VL_ATTR_ALIGNED(128);

#endif  /*guard*/
