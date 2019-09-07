// Verilated -*- C++ -*-
// DESCRIPTION: Verilator output: Symbol table internal header
//
// Internal details; most calling programs do not need this header

#ifndef _VTestAccel2__Syms_H_
#define _VTestAccel2__Syms_H_

#include "verilated.h"

// INCLUDE MODULE CLASSES
#include "VTestAccel2.h"

// DPI TYPES for DPI Export callbacks (Internal use)

// SYMS CLASS
class VTestAccel2__Syms : public VerilatedSyms {
  public:
    
    // LOCAL STATE
    const char* __Vm_namep;
    bool	__Vm_activity;		///< Used by trace routines to determine change occurred
    bool	__Vm_didInit;
    //char	__VpadToAlign10[6];
    
    // SUBCELL STATE
    VTestAccel2*                   TOPp;
    
    // COVERAGE
    
    // SCOPE NAMES
    
    // CREATORS
    VTestAccel2__Syms(VTestAccel2* topp, const char* namep);
    ~VTestAccel2__Syms() {};
    
    // METHODS
    inline const char* name() { return __Vm_namep; }
    inline bool getClearActivity() { bool r=__Vm_activity; __Vm_activity=false; return r;}
    
} VL_ATTR_ALIGNED(64);

#endif  /*guard*/
