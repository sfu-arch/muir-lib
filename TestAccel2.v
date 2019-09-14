module VTASim(
  input   clock,
  input   reset,
  output  sim_wait
);
  wire  sim_clock; // @[SimShell.scala 74:19]
  wire  sim_reset; // @[SimShell.scala 74:19]
  wire  sim_dpi_wait; // @[SimShell.scala 74:19]
  VTASimDPI sim ( // @[SimShell.scala 74:19]
    .clock(sim_clock),
    .reset(sim_reset),
    .dpi_wait(sim_dpi_wait)
  );
  assign sim_wait = sim_dpi_wait; // @[SimShell.scala 77:12]
  assign sim_clock = clock; // @[SimShell.scala 76:16]
  assign sim_reset = $unsigned(reset); // @[SimShell.scala 75:16]
endmodule
module VTAHostDPIToAXI(
  input         clock,
  input         reset,
  input         io_dpi_req_valid,
  input         io_dpi_req_opcode,
  input  [7:0]  io_dpi_req_addr,
  input  [31:0] io_dpi_req_value,
  output        io_dpi_req_deq,
  output        io_dpi_resp_valid,
  output [31:0] io_dpi_resp_bits,
  input         io_axi_aw_ready,
  output        io_axi_aw_valid,
  output [31:0] io_axi_aw_bits_addr,
  input         io_axi_w_ready,
  output        io_axi_w_valid,
  output [31:0] io_axi_w_bits_data,
  output        io_axi_b_ready,
  input         io_axi_b_valid,
  input         io_axi_ar_ready,
  output        io_axi_ar_valid,
  output [31:0] io_axi_ar_bits_addr,
  output        io_axi_r_ready,
  input         io_axi_r_valid,
  input  [31:0] io_axi_r_bits_data
);
  reg [7:0] addr; // @[VTAHostDPI.scala 87:21]
  reg [31:0] _RAND_0;
  reg [31:0] data; // @[VTAHostDPI.scala 88:21]
  reg [31:0] _RAND_1;
  reg [2:0] state; // @[VTAHostDPI.scala 90:22]
  reg [31:0] _RAND_2;
  wire  _T_2; // @[Conditional.scala 37:30]
  wire  _T_3; // @[Conditional.scala 37:30]
  wire  _T_4; // @[Conditional.scala 37:30]
  wire  _T_5; // @[Conditional.scala 37:30]
  wire  _T_6; // @[Conditional.scala 37:30]
  wire  _T_7; // @[Conditional.scala 37:30]
  wire  _T_8; // @[VTAHostDPI.scala 129:15]
  wire  _T_9; // @[VTAHostDPI.scala 129:25]
  wire  _T_10; // @[VTAHostDPI.scala 134:28]
  wire  _T_13; // @[VTAHostDPI.scala 141:28]
  wire  _T_16; // @[VTAHostDPI.scala 145:45]
  wire  _T_18; // @[VTAHostDPI.scala 145:91]
  assign _T_2 = 3'h0 == state; // @[Conditional.scala 37:30]
  assign _T_3 = 3'h1 == state; // @[Conditional.scala 37:30]
  assign _T_4 = 3'h2 == state; // @[Conditional.scala 37:30]
  assign _T_5 = 3'h3 == state; // @[Conditional.scala 37:30]
  assign _T_6 = 3'h4 == state; // @[Conditional.scala 37:30]
  assign _T_7 = 3'h5 == state; // @[Conditional.scala 37:30]
  assign _T_8 = state == 3'h0; // @[VTAHostDPI.scala 129:15]
  assign _T_9 = _T_8 & io_dpi_req_valid; // @[VTAHostDPI.scala 129:25]
  assign _T_10 = state == 3'h3; // @[VTAHostDPI.scala 134:28]
  assign _T_13 = state == 3'h1; // @[VTAHostDPI.scala 141:28]
  assign _T_16 = _T_13 & io_axi_ar_ready; // @[VTAHostDPI.scala 145:45]
  assign _T_18 = _T_10 & io_axi_aw_ready; // @[VTAHostDPI.scala 145:91]
  assign io_dpi_req_deq = _T_16 | _T_18; // @[VTAHostDPI.scala 145:18]
  assign io_dpi_resp_valid = io_axi_r_valid; // @[VTAHostDPI.scala 146:21]
  assign io_dpi_resp_bits = io_axi_r_bits_data; // @[VTAHostDPI.scala 147:20]
  assign io_axi_aw_valid = state == 3'h3; // @[VTAHostDPI.scala 134:19]
  assign io_axi_aw_bits_addr = {{24'd0}, addr}; // @[VTAHostDPI.scala 135:23]
  assign io_axi_w_valid = state == 3'h4; // @[VTAHostDPI.scala 136:18]
  assign io_axi_w_bits_data = data; // @[VTAHostDPI.scala 137:22]
  assign io_axi_b_ready = state == 3'h5; // @[VTAHostDPI.scala 139:18]
  assign io_axi_ar_valid = state == 3'h1; // @[VTAHostDPI.scala 141:19]
  assign io_axi_ar_bits_addr = {{24'd0}, addr}; // @[VTAHostDPI.scala 142:23]
  assign io_axi_r_ready = state == 3'h2; // @[VTAHostDPI.scala 143:18]
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  addr = _RAND_0[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  data = _RAND_1[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  state = _RAND_2[2:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end
  always @(posedge clock) begin
    if (reset) begin
      addr <= 8'h0;
    end else begin
      if (_T_9) begin
        addr <= io_dpi_req_addr;
      end
    end
    if (reset) begin
      data <= 32'h0;
    end else begin
      if (_T_9) begin
        data <= io_dpi_req_value;
      end
    end
    if (reset) begin
      state <= 3'h0;
    end else begin
      if (_T_2) begin
        if (io_dpi_req_valid) begin
          if (io_dpi_req_opcode) begin
            state <= 3'h3;
          end else begin
            state <= 3'h1;
          end
        end
      end else begin
        if (_T_3) begin
          if (io_axi_ar_ready) begin
            state <= 3'h2;
          end
        end else begin
          if (_T_4) begin
            if (io_axi_r_valid) begin
              state <= 3'h0;
            end
          end else begin
            if (_T_5) begin
              if (io_axi_aw_ready) begin
                state <= 3'h4;
              end
            end else begin
              if (_T_6) begin
                if (io_axi_w_ready) begin
                  state <= 3'h5;
                end
              end else begin
                if (_T_7) begin
                  if (io_axi_b_valid) begin
                    state <= 3'h0;
                  end
                end
              end
            end
          end
        end
      end
    end
  end
endmodule
module VTAHost(
  input         clock,
  input         reset,
  input         io_axi_aw_ready,
  output        io_axi_aw_valid,
  output [31:0] io_axi_aw_bits_addr,
  input         io_axi_w_ready,
  output        io_axi_w_valid,
  output [31:0] io_axi_w_bits_data,
  output        io_axi_b_ready,
  input         io_axi_b_valid,
  input         io_axi_ar_ready,
  output        io_axi_ar_valid,
  output [31:0] io_axi_ar_bits_addr,
  output        io_axi_r_ready,
  input         io_axi_r_valid,
  input  [31:0] io_axi_r_bits_data
);
  wire  host_dpi_clock; // @[SimShell.scala 39:24]
  wire  host_dpi_reset; // @[SimShell.scala 39:24]
  wire  host_dpi_dpi_req_valid; // @[SimShell.scala 39:24]
  wire  host_dpi_dpi_req_opcode; // @[SimShell.scala 39:24]
  wire [7:0] host_dpi_dpi_req_addr; // @[SimShell.scala 39:24]
  wire [31:0] host_dpi_dpi_req_value; // @[SimShell.scala 39:24]
  wire  host_dpi_dpi_req_deq; // @[SimShell.scala 39:24]
  wire  host_dpi_dpi_resp_valid; // @[SimShell.scala 39:24]
  wire [31:0] host_dpi_dpi_resp_bits; // @[SimShell.scala 39:24]
  wire  host_axi_clock; // @[SimShell.scala 40:24]
  wire  host_axi_reset; // @[SimShell.scala 40:24]
  wire  host_axi_io_dpi_req_valid; // @[SimShell.scala 40:24]
  wire  host_axi_io_dpi_req_opcode; // @[SimShell.scala 40:24]
  wire [7:0] host_axi_io_dpi_req_addr; // @[SimShell.scala 40:24]
  wire [31:0] host_axi_io_dpi_req_value; // @[SimShell.scala 40:24]
  wire  host_axi_io_dpi_req_deq; // @[SimShell.scala 40:24]
  wire  host_axi_io_dpi_resp_valid; // @[SimShell.scala 40:24]
  wire [31:0] host_axi_io_dpi_resp_bits; // @[SimShell.scala 40:24]
  wire  host_axi_io_axi_aw_ready; // @[SimShell.scala 40:24]
  wire  host_axi_io_axi_aw_valid; // @[SimShell.scala 40:24]
  wire [31:0] host_axi_io_axi_aw_bits_addr; // @[SimShell.scala 40:24]
  wire  host_axi_io_axi_w_ready; // @[SimShell.scala 40:24]
  wire  host_axi_io_axi_w_valid; // @[SimShell.scala 40:24]
  wire [31:0] host_axi_io_axi_w_bits_data; // @[SimShell.scala 40:24]
  wire  host_axi_io_axi_b_ready; // @[SimShell.scala 40:24]
  wire  host_axi_io_axi_b_valid; // @[SimShell.scala 40:24]
  wire  host_axi_io_axi_ar_ready; // @[SimShell.scala 40:24]
  wire  host_axi_io_axi_ar_valid; // @[SimShell.scala 40:24]
  wire [31:0] host_axi_io_axi_ar_bits_addr; // @[SimShell.scala 40:24]
  wire  host_axi_io_axi_r_ready; // @[SimShell.scala 40:24]
  wire  host_axi_io_axi_r_valid; // @[SimShell.scala 40:24]
  wire [31:0] host_axi_io_axi_r_bits_data; // @[SimShell.scala 40:24]
  VTAHostDPI host_dpi ( // @[SimShell.scala 39:24]
    .clock(host_dpi_clock),
    .reset(host_dpi_reset),
    .dpi_req_valid(host_dpi_dpi_req_valid),
    .dpi_req_opcode(host_dpi_dpi_req_opcode),
    .dpi_req_addr(host_dpi_dpi_req_addr),
    .dpi_req_value(host_dpi_dpi_req_value),
    .dpi_req_deq(host_dpi_dpi_req_deq),
    .dpi_resp_valid(host_dpi_dpi_resp_valid),
    .dpi_resp_bits(host_dpi_dpi_resp_bits)
  );
  VTAHostDPIToAXI host_axi ( // @[SimShell.scala 40:24]
    .clock(host_axi_clock),
    .reset(host_axi_reset),
    .io_dpi_req_valid(host_axi_io_dpi_req_valid),
    .io_dpi_req_opcode(host_axi_io_dpi_req_opcode),
    .io_dpi_req_addr(host_axi_io_dpi_req_addr),
    .io_dpi_req_value(host_axi_io_dpi_req_value),
    .io_dpi_req_deq(host_axi_io_dpi_req_deq),
    .io_dpi_resp_valid(host_axi_io_dpi_resp_valid),
    .io_dpi_resp_bits(host_axi_io_dpi_resp_bits),
    .io_axi_aw_ready(host_axi_io_axi_aw_ready),
    .io_axi_aw_valid(host_axi_io_axi_aw_valid),
    .io_axi_aw_bits_addr(host_axi_io_axi_aw_bits_addr),
    .io_axi_w_ready(host_axi_io_axi_w_ready),
    .io_axi_w_valid(host_axi_io_axi_w_valid),
    .io_axi_w_bits_data(host_axi_io_axi_w_bits_data),
    .io_axi_b_ready(host_axi_io_axi_b_ready),
    .io_axi_b_valid(host_axi_io_axi_b_valid),
    .io_axi_ar_ready(host_axi_io_axi_ar_ready),
    .io_axi_ar_valid(host_axi_io_axi_ar_valid),
    .io_axi_ar_bits_addr(host_axi_io_axi_ar_bits_addr),
    .io_axi_r_ready(host_axi_io_axi_r_ready),
    .io_axi_r_valid(host_axi_io_axi_r_valid),
    .io_axi_r_bits_data(host_axi_io_axi_r_bits_data)
  );
  assign io_axi_aw_valid = host_axi_io_axi_aw_valid; // @[SimShell.scala 44:10]
  assign io_axi_aw_bits_addr = host_axi_io_axi_aw_bits_addr; // @[SimShell.scala 44:10]
  assign io_axi_w_valid = host_axi_io_axi_w_valid; // @[SimShell.scala 44:10]
  assign io_axi_w_bits_data = host_axi_io_axi_w_bits_data; // @[SimShell.scala 44:10]
  assign io_axi_b_ready = host_axi_io_axi_b_ready; // @[SimShell.scala 44:10]
  assign io_axi_ar_valid = host_axi_io_axi_ar_valid; // @[SimShell.scala 44:10]
  assign io_axi_ar_bits_addr = host_axi_io_axi_ar_bits_addr; // @[SimShell.scala 44:10]
  assign io_axi_r_ready = host_axi_io_axi_r_ready; // @[SimShell.scala 44:10]
  assign host_dpi_clock = clock; // @[SimShell.scala 42:21]
  assign host_dpi_reset = reset; // @[SimShell.scala 41:21]
  assign host_dpi_dpi_req_deq = host_axi_io_dpi_req_deq; // @[SimShell.scala 43:19]
  assign host_dpi_dpi_resp_valid = host_axi_io_dpi_resp_valid; // @[SimShell.scala 43:19]
  assign host_dpi_dpi_resp_bits = host_axi_io_dpi_resp_bits; // @[SimShell.scala 43:19]
  assign host_axi_clock = clock;
  assign host_axi_reset = reset;
  assign host_axi_io_dpi_req_valid = host_dpi_dpi_req_valid; // @[SimShell.scala 43:19]
  assign host_axi_io_dpi_req_opcode = host_dpi_dpi_req_opcode; // @[SimShell.scala 43:19]
  assign host_axi_io_dpi_req_addr = host_dpi_dpi_req_addr; // @[SimShell.scala 43:19]
  assign host_axi_io_dpi_req_value = host_dpi_dpi_req_value; // @[SimShell.scala 43:19]
  assign host_axi_io_axi_aw_ready = io_axi_aw_ready; // @[SimShell.scala 44:10]
  assign host_axi_io_axi_w_ready = io_axi_w_ready; // @[SimShell.scala 44:10]
  assign host_axi_io_axi_b_valid = io_axi_b_valid; // @[SimShell.scala 44:10]
  assign host_axi_io_axi_ar_ready = io_axi_ar_ready; // @[SimShell.scala 44:10]
  assign host_axi_io_axi_r_valid = io_axi_r_valid; // @[SimShell.scala 44:10]
  assign host_axi_io_axi_r_bits_data = io_axi_r_bits_data; // @[SimShell.scala 44:10]
endmodule
module VTAMemDPIToAXI(
  input         clock,
  input         reset,
  output        io_dpi_req_valid,
  output        io_dpi_req_opcode,
  output [7:0]  io_dpi_req_len,
  output [63:0] io_dpi_req_addr,
  output        io_dpi_wr_valid,
  output [63:0] io_dpi_wr_bits,
  output        io_dpi_rd_ready,
  input         io_dpi_rd_valid,
  input  [63:0] io_dpi_rd_bits,
  output        io_axi_aw_ready,
  input         io_axi_aw_valid,
  input  [31:0] io_axi_aw_bits_addr,
  input  [7:0]  io_axi_aw_bits_len,
  output        io_axi_w_ready,
  input         io_axi_w_valid,
  input  [63:0] io_axi_w_bits_data,
  input         io_axi_w_bits_last,
  input         io_axi_b_ready,
  output        io_axi_b_valid,
  output        io_axi_ar_ready,
  input         io_axi_ar_valid,
  input  [31:0] io_axi_ar_bits_addr,
  input  [7:0]  io_axi_ar_bits_len,
  input         io_axi_r_ready,
  output        io_axi_r_valid,
  output [63:0] io_axi_r_bits_data,
  output        io_axi_r_bits_last
);
  reg  opcode; // @[VTAMemDPI.scala 83:23]
  reg [31:0] _RAND_0;
  reg [7:0] len; // @[VTAMemDPI.scala 84:20]
  reg [31:0] _RAND_1;
  reg [63:0] addr; // @[VTAMemDPI.scala 85:21]
  reg [63:0] _RAND_2;
  reg [2:0] state; // @[VTAMemDPI.scala 87:22]
  reg [31:0] _RAND_3;
  wire  _T_2; // @[Conditional.scala 37:30]
  wire  _T_3; // @[Conditional.scala 37:30]
  wire  _T_4; // @[Conditional.scala 37:30]
  wire  _T_5; // @[VTAMemDPI.scala 103:28]
  wire  _T_6; // @[VTAMemDPI.scala 103:54]
  wire  _T_7; // @[VTAMemDPI.scala 103:47]
  wire  _T_8; // @[Conditional.scala 37:30]
  wire  _T_9; // @[Conditional.scala 37:30]
  wire  _T_10; // @[VTAMemDPI.scala 113:28]
  wire  _T_11; // @[Conditional.scala 37:30]
  wire  _T_12; // @[VTAMemDPI.scala 124:15]
  wire  _GEN_13; // @[VTAMemDPI.scala 129:35]
  wire  _T_13; // @[VTAMemDPI.scala 134:22]
  wire  _T_15; // @[VTAMemDPI.scala 135:52]
  wire  _T_16; // @[VTAMemDPI.scala 135:45]
  wire [7:0] _T_18; // @[VTAMemDPI.scala 136:18]
  wire  _T_19; // @[VTAMemDPI.scala 140:30]
  wire  _T_20; // @[VTAMemDPI.scala 140:47]
  wire  _T_21; // @[VTAMemDPI.scala 140:75]
  wire  _T_22; // @[VTAMemDPI.scala 140:93]
  wire  _T_31; // @[VTAMemDPI.scala 156:28]
  assign _T_2 = 3'h0 == state; // @[Conditional.scala 37:30]
  assign _T_3 = 3'h1 == state; // @[Conditional.scala 37:30]
  assign _T_4 = 3'h2 == state; // @[Conditional.scala 37:30]
  assign _T_5 = io_axi_r_ready & io_dpi_rd_valid; // @[VTAMemDPI.scala 103:28]
  assign _T_6 = len == 8'h0; // @[VTAMemDPI.scala 103:54]
  assign _T_7 = _T_5 & _T_6; // @[VTAMemDPI.scala 103:47]
  assign _T_8 = 3'h3 == state; // @[Conditional.scala 37:30]
  assign _T_9 = 3'h4 == state; // @[Conditional.scala 37:30]
  assign _T_10 = io_axi_w_valid & io_axi_w_bits_last; // @[VTAMemDPI.scala 113:28]
  assign _T_11 = 3'h5 == state; // @[Conditional.scala 37:30]
  assign _T_12 = state == 3'h0; // @[VTAMemDPI.scala 124:15]
  assign _GEN_13 = io_axi_aw_valid | opcode; // @[VTAMemDPI.scala 129:35]
  assign _T_13 = state == 3'h2; // @[VTAMemDPI.scala 134:22]
  assign _T_15 = len != 8'h0; // @[VTAMemDPI.scala 135:52]
  assign _T_16 = _T_5 & _T_15; // @[VTAMemDPI.scala 135:45]
  assign _T_18 = len - 8'h1; // @[VTAMemDPI.scala 136:18]
  assign _T_19 = state == 3'h1; // @[VTAMemDPI.scala 140:30]
  assign _T_20 = _T_19 & io_axi_ar_valid; // @[VTAMemDPI.scala 140:47]
  assign _T_21 = state == 3'h3; // @[VTAMemDPI.scala 140:75]
  assign _T_22 = _T_21 & io_axi_aw_valid; // @[VTAMemDPI.scala 140:93]
  assign _T_31 = state == 3'h4; // @[VTAMemDPI.scala 156:28]
  assign io_dpi_req_valid = _T_20 | _T_22; // @[VTAMemDPI.scala 140:20]
  assign io_dpi_req_opcode = opcode; // @[VTAMemDPI.scala 141:21]
  assign io_dpi_req_len = len; // @[VTAMemDPI.scala 142:18]
  assign io_dpi_req_addr = addr; // @[VTAMemDPI.scala 143:19]
  assign io_dpi_wr_valid = _T_31 & io_axi_w_valid; // @[VTAMemDPI.scala 156:19]
  assign io_dpi_wr_bits = io_axi_w_bits_data; // @[VTAMemDPI.scala 157:18]
  assign io_dpi_rd_ready = _T_13 & io_axi_r_ready; // @[VTAMemDPI.scala 154:19]
  assign io_axi_aw_ready = state == 3'h3; // @[VTAMemDPI.scala 146:19]
  assign io_axi_w_ready = state == 3'h4; // @[VTAMemDPI.scala 158:18]
  assign io_axi_b_valid = state == 3'h5; // @[VTAMemDPI.scala 160:18]
  assign io_axi_ar_ready = state == 3'h1; // @[VTAMemDPI.scala 145:19]
  assign io_axi_r_valid = _T_13 & io_dpi_rd_valid; // @[VTAMemDPI.scala 148:18]
  assign io_axi_r_bits_data = io_dpi_rd_bits; // @[VTAMemDPI.scala 149:22]
  assign io_axi_r_bits_last = len == 8'h0; // @[VTAMemDPI.scala 150:22]
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  opcode = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  len = _RAND_1[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {2{`RANDOM}};
  addr = _RAND_2[63:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  state = _RAND_3[2:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end
  always @(posedge clock) begin
    if (reset) begin
      opcode <= 1'h0;
    end else begin
      if (_T_12) begin
        if (io_axi_ar_valid) begin
          opcode <= 1'h0;
        end else begin
          opcode <= _GEN_13;
        end
      end
    end
    if (reset) begin
      len <= 8'h0;
    end else begin
      if (_T_12) begin
        if (io_axi_ar_valid) begin
          len <= io_axi_ar_bits_len;
        end else begin
          if (io_axi_aw_valid) begin
            len <= io_axi_aw_bits_len;
          end
        end
      end else begin
        if (_T_13) begin
          if (_T_16) begin
            len <= _T_18;
          end
        end
      end
    end
    if (reset) begin
      addr <= 64'h0;
    end else begin
      if (_T_12) begin
        if (io_axi_ar_valid) begin
          addr <= {{32'd0}, io_axi_ar_bits_addr};
        end else begin
          if (io_axi_aw_valid) begin
            addr <= {{32'd0}, io_axi_aw_bits_addr};
          end
        end
      end
    end
    if (reset) begin
      state <= 3'h0;
    end else begin
      if (_T_2) begin
        if (io_axi_ar_valid) begin
          state <= 3'h1;
        end else begin
          if (io_axi_aw_valid) begin
            state <= 3'h3;
          end
        end
      end else begin
        if (_T_3) begin
          if (io_axi_ar_valid) begin
            state <= 3'h2;
          end
        end else begin
          if (_T_4) begin
            if (_T_7) begin
              state <= 3'h0;
            end
          end else begin
            if (_T_8) begin
              if (io_axi_aw_valid) begin
                state <= 3'h4;
              end
            end else begin
              if (_T_9) begin
                if (_T_10) begin
                  state <= 3'h5;
                end
              end else begin
                if (_T_11) begin
                  if (io_axi_b_ready) begin
                    state <= 3'h0;
                  end
                end
              end
            end
          end
        end
      end
    end
  end
endmodule
module VTAMem(
  input         clock,
  input         reset,
  output        io_axi_aw_ready,
  input         io_axi_aw_valid,
  input  [31:0] io_axi_aw_bits_addr,
  input  [7:0]  io_axi_aw_bits_len,
  output        io_axi_w_ready,
  input         io_axi_w_valid,
  input  [63:0] io_axi_w_bits_data,
  input         io_axi_w_bits_last,
  input         io_axi_b_ready,
  output        io_axi_b_valid,
  output        io_axi_ar_ready,
  input         io_axi_ar_valid,
  input  [31:0] io_axi_ar_bits_addr,
  input  [7:0]  io_axi_ar_bits_len,
  input         io_axi_r_ready,
  output        io_axi_r_valid,
  output [63:0] io_axi_r_bits_data,
  output        io_axi_r_bits_last
);
  wire  mem_dpi_clock; // @[SimShell.scala 57:23]
  wire  mem_dpi_reset; // @[SimShell.scala 57:23]
  wire  mem_dpi_dpi_req_valid; // @[SimShell.scala 57:23]
  wire  mem_dpi_dpi_req_opcode; // @[SimShell.scala 57:23]
  wire [7:0] mem_dpi_dpi_req_len; // @[SimShell.scala 57:23]
  wire [63:0] mem_dpi_dpi_req_addr; // @[SimShell.scala 57:23]
  wire  mem_dpi_dpi_wr_valid; // @[SimShell.scala 57:23]
  wire [63:0] mem_dpi_dpi_wr_bits; // @[SimShell.scala 57:23]
  wire  mem_dpi_dpi_rd_ready; // @[SimShell.scala 57:23]
  wire  mem_dpi_dpi_rd_valid; // @[SimShell.scala 57:23]
  wire [63:0] mem_dpi_dpi_rd_bits; // @[SimShell.scala 57:23]
  wire  mem_axi_clock; // @[SimShell.scala 58:23]
  wire  mem_axi_reset; // @[SimShell.scala 58:23]
  wire  mem_axi_io_dpi_req_valid; // @[SimShell.scala 58:23]
  wire  mem_axi_io_dpi_req_opcode; // @[SimShell.scala 58:23]
  wire [7:0] mem_axi_io_dpi_req_len; // @[SimShell.scala 58:23]
  wire [63:0] mem_axi_io_dpi_req_addr; // @[SimShell.scala 58:23]
  wire  mem_axi_io_dpi_wr_valid; // @[SimShell.scala 58:23]
  wire [63:0] mem_axi_io_dpi_wr_bits; // @[SimShell.scala 58:23]
  wire  mem_axi_io_dpi_rd_ready; // @[SimShell.scala 58:23]
  wire  mem_axi_io_dpi_rd_valid; // @[SimShell.scala 58:23]
  wire [63:0] mem_axi_io_dpi_rd_bits; // @[SimShell.scala 58:23]
  wire  mem_axi_io_axi_aw_ready; // @[SimShell.scala 58:23]
  wire  mem_axi_io_axi_aw_valid; // @[SimShell.scala 58:23]
  wire [31:0] mem_axi_io_axi_aw_bits_addr; // @[SimShell.scala 58:23]
  wire [7:0] mem_axi_io_axi_aw_bits_len; // @[SimShell.scala 58:23]
  wire  mem_axi_io_axi_w_ready; // @[SimShell.scala 58:23]
  wire  mem_axi_io_axi_w_valid; // @[SimShell.scala 58:23]
  wire [63:0] mem_axi_io_axi_w_bits_data; // @[SimShell.scala 58:23]
  wire  mem_axi_io_axi_w_bits_last; // @[SimShell.scala 58:23]
  wire  mem_axi_io_axi_b_ready; // @[SimShell.scala 58:23]
  wire  mem_axi_io_axi_b_valid; // @[SimShell.scala 58:23]
  wire  mem_axi_io_axi_ar_ready; // @[SimShell.scala 58:23]
  wire  mem_axi_io_axi_ar_valid; // @[SimShell.scala 58:23]
  wire [31:0] mem_axi_io_axi_ar_bits_addr; // @[SimShell.scala 58:23]
  wire [7:0] mem_axi_io_axi_ar_bits_len; // @[SimShell.scala 58:23]
  wire  mem_axi_io_axi_r_ready; // @[SimShell.scala 58:23]
  wire  mem_axi_io_axi_r_valid; // @[SimShell.scala 58:23]
  wire [63:0] mem_axi_io_axi_r_bits_data; // @[SimShell.scala 58:23]
  wire  mem_axi_io_axi_r_bits_last; // @[SimShell.scala 58:23]
  VTAMemDPI mem_dpi ( // @[SimShell.scala 57:23]
    .clock(mem_dpi_clock),
    .reset(mem_dpi_reset),
    .dpi_req_valid(mem_dpi_dpi_req_valid),
    .dpi_req_opcode(mem_dpi_dpi_req_opcode),
    .dpi_req_len(mem_dpi_dpi_req_len),
    .dpi_req_addr(mem_dpi_dpi_req_addr),
    .dpi_wr_valid(mem_dpi_dpi_wr_valid),
    .dpi_wr_bits(mem_dpi_dpi_wr_bits),
    .dpi_rd_ready(mem_dpi_dpi_rd_ready),
    .dpi_rd_valid(mem_dpi_dpi_rd_valid),
    .dpi_rd_bits(mem_dpi_dpi_rd_bits)
  );
  VTAMemDPIToAXI mem_axi ( // @[SimShell.scala 58:23]
    .clock(mem_axi_clock),
    .reset(mem_axi_reset),
    .io_dpi_req_valid(mem_axi_io_dpi_req_valid),
    .io_dpi_req_opcode(mem_axi_io_dpi_req_opcode),
    .io_dpi_req_len(mem_axi_io_dpi_req_len),
    .io_dpi_req_addr(mem_axi_io_dpi_req_addr),
    .io_dpi_wr_valid(mem_axi_io_dpi_wr_valid),
    .io_dpi_wr_bits(mem_axi_io_dpi_wr_bits),
    .io_dpi_rd_ready(mem_axi_io_dpi_rd_ready),
    .io_dpi_rd_valid(mem_axi_io_dpi_rd_valid),
    .io_dpi_rd_bits(mem_axi_io_dpi_rd_bits),
    .io_axi_aw_ready(mem_axi_io_axi_aw_ready),
    .io_axi_aw_valid(mem_axi_io_axi_aw_valid),
    .io_axi_aw_bits_addr(mem_axi_io_axi_aw_bits_addr),
    .io_axi_aw_bits_len(mem_axi_io_axi_aw_bits_len),
    .io_axi_w_ready(mem_axi_io_axi_w_ready),
    .io_axi_w_valid(mem_axi_io_axi_w_valid),
    .io_axi_w_bits_data(mem_axi_io_axi_w_bits_data),
    .io_axi_w_bits_last(mem_axi_io_axi_w_bits_last),
    .io_axi_b_ready(mem_axi_io_axi_b_ready),
    .io_axi_b_valid(mem_axi_io_axi_b_valid),
    .io_axi_ar_ready(mem_axi_io_axi_ar_ready),
    .io_axi_ar_valid(mem_axi_io_axi_ar_valid),
    .io_axi_ar_bits_addr(mem_axi_io_axi_ar_bits_addr),
    .io_axi_ar_bits_len(mem_axi_io_axi_ar_bits_len),
    .io_axi_r_ready(mem_axi_io_axi_r_ready),
    .io_axi_r_valid(mem_axi_io_axi_r_valid),
    .io_axi_r_bits_data(mem_axi_io_axi_r_bits_data),
    .io_axi_r_bits_last(mem_axi_io_axi_r_bits_last)
  );
  assign io_axi_aw_ready = mem_axi_io_axi_aw_ready; // @[SimShell.scala 62:10]
  assign io_axi_w_ready = mem_axi_io_axi_w_ready; // @[SimShell.scala 62:10]
  assign io_axi_b_valid = mem_axi_io_axi_b_valid; // @[SimShell.scala 62:10]
  assign io_axi_ar_ready = mem_axi_io_axi_ar_ready; // @[SimShell.scala 62:10]
  assign io_axi_r_valid = mem_axi_io_axi_r_valid; // @[SimShell.scala 62:10]
  assign io_axi_r_bits_data = mem_axi_io_axi_r_bits_data; // @[SimShell.scala 62:10]
  assign io_axi_r_bits_last = mem_axi_io_axi_r_bits_last; // @[SimShell.scala 62:10]
  assign mem_dpi_clock = clock; // @[SimShell.scala 60:20]
  assign mem_dpi_reset = reset; // @[SimShell.scala 59:20]
  assign mem_dpi_dpi_req_valid = mem_axi_io_dpi_req_valid; // @[SimShell.scala 61:18]
  assign mem_dpi_dpi_req_opcode = mem_axi_io_dpi_req_opcode; // @[SimShell.scala 61:18]
  assign mem_dpi_dpi_req_len = mem_axi_io_dpi_req_len; // @[SimShell.scala 61:18]
  assign mem_dpi_dpi_req_addr = mem_axi_io_dpi_req_addr; // @[SimShell.scala 61:18]
  assign mem_dpi_dpi_wr_valid = mem_axi_io_dpi_wr_valid; // @[SimShell.scala 61:18]
  assign mem_dpi_dpi_wr_bits = mem_axi_io_dpi_wr_bits; // @[SimShell.scala 61:18]
  assign mem_dpi_dpi_rd_ready = mem_axi_io_dpi_rd_ready; // @[SimShell.scala 61:18]
  assign mem_axi_clock = clock;
  assign mem_axi_reset = reset;
  assign mem_axi_io_dpi_rd_valid = mem_dpi_dpi_rd_valid; // @[SimShell.scala 61:18]
  assign mem_axi_io_dpi_rd_bits = mem_dpi_dpi_rd_bits; // @[SimShell.scala 61:18]
  assign mem_axi_io_axi_aw_valid = io_axi_aw_valid; // @[SimShell.scala 62:10]
  assign mem_axi_io_axi_aw_bits_addr = io_axi_aw_bits_addr; // @[SimShell.scala 62:10]
  assign mem_axi_io_axi_aw_bits_len = io_axi_aw_bits_len; // @[SimShell.scala 62:10]
  assign mem_axi_io_axi_w_valid = io_axi_w_valid; // @[SimShell.scala 62:10]
  assign mem_axi_io_axi_w_bits_data = io_axi_w_bits_data; // @[SimShell.scala 62:10]
  assign mem_axi_io_axi_w_bits_last = io_axi_w_bits_last; // @[SimShell.scala 62:10]
  assign mem_axi_io_axi_b_ready = io_axi_b_ready; // @[SimShell.scala 62:10]
  assign mem_axi_io_axi_ar_valid = io_axi_ar_valid; // @[SimShell.scala 62:10]
  assign mem_axi_io_axi_ar_bits_addr = io_axi_ar_bits_addr; // @[SimShell.scala 62:10]
  assign mem_axi_io_axi_ar_bits_len = io_axi_ar_bits_len; // @[SimShell.scala 62:10]
  assign mem_axi_io_axi_r_ready = io_axi_r_ready; // @[SimShell.scala 62:10]
endmodule
module AXISimShell(
  input         clock,
  input         reset,
  output        mem_aw_ready,
  input         mem_aw_valid,
  input  [31:0] mem_aw_bits_addr,
  input  [7:0]  mem_aw_bits_len,
  output        mem_w_ready,
  input         mem_w_valid,
  input  [63:0] mem_w_bits_data,
  input         mem_w_bits_last,
  input         mem_b_ready,
  output        mem_b_valid,
  output        mem_ar_ready,
  input         mem_ar_valid,
  input  [31:0] mem_ar_bits_addr,
  input  [7:0]  mem_ar_bits_len,
  input         mem_r_ready,
  output        mem_r_valid,
  output [63:0] mem_r_bits_data,
  output        mem_r_bits_last,
  input         host_aw_ready,
  output        host_aw_valid,
  output [31:0] host_aw_bits_addr,
  input         host_w_ready,
  output        host_w_valid,
  output [31:0] host_w_bits_data,
  output        host_b_ready,
  input         host_b_valid,
  input         host_ar_ready,
  output        host_ar_valid,
  output [31:0] host_ar_bits_addr,
  output        host_r_ready,
  input         host_r_valid,
  input  [31:0] host_r_bits_data,
  input         sim_clock,
  output        sim_wait
);
  wire  mod_sim_clock; // @[SimShell.scala 90:23]
  wire  mod_sim_reset; // @[SimShell.scala 90:23]
  wire  mod_sim_sim_wait; // @[SimShell.scala 90:23]
  wire  mod_host_clock; // @[SimShell.scala 91:24]
  wire  mod_host_reset; // @[SimShell.scala 91:24]
  wire  mod_host_io_axi_aw_ready; // @[SimShell.scala 91:24]
  wire  mod_host_io_axi_aw_valid; // @[SimShell.scala 91:24]
  wire [31:0] mod_host_io_axi_aw_bits_addr; // @[SimShell.scala 91:24]
  wire  mod_host_io_axi_w_ready; // @[SimShell.scala 91:24]
  wire  mod_host_io_axi_w_valid; // @[SimShell.scala 91:24]
  wire [31:0] mod_host_io_axi_w_bits_data; // @[SimShell.scala 91:24]
  wire  mod_host_io_axi_b_ready; // @[SimShell.scala 91:24]
  wire  mod_host_io_axi_b_valid; // @[SimShell.scala 91:24]
  wire  mod_host_io_axi_ar_ready; // @[SimShell.scala 91:24]
  wire  mod_host_io_axi_ar_valid; // @[SimShell.scala 91:24]
  wire [31:0] mod_host_io_axi_ar_bits_addr; // @[SimShell.scala 91:24]
  wire  mod_host_io_axi_r_ready; // @[SimShell.scala 91:24]
  wire  mod_host_io_axi_r_valid; // @[SimShell.scala 91:24]
  wire [31:0] mod_host_io_axi_r_bits_data; // @[SimShell.scala 91:24]
  wire  mod_mem_clock; // @[SimShell.scala 92:23]
  wire  mod_mem_reset; // @[SimShell.scala 92:23]
  wire  mod_mem_io_axi_aw_ready; // @[SimShell.scala 92:23]
  wire  mod_mem_io_axi_aw_valid; // @[SimShell.scala 92:23]
  wire [31:0] mod_mem_io_axi_aw_bits_addr; // @[SimShell.scala 92:23]
  wire [7:0] mod_mem_io_axi_aw_bits_len; // @[SimShell.scala 92:23]
  wire  mod_mem_io_axi_w_ready; // @[SimShell.scala 92:23]
  wire  mod_mem_io_axi_w_valid; // @[SimShell.scala 92:23]
  wire [63:0] mod_mem_io_axi_w_bits_data; // @[SimShell.scala 92:23]
  wire  mod_mem_io_axi_w_bits_last; // @[SimShell.scala 92:23]
  wire  mod_mem_io_axi_b_ready; // @[SimShell.scala 92:23]
  wire  mod_mem_io_axi_b_valid; // @[SimShell.scala 92:23]
  wire  mod_mem_io_axi_ar_ready; // @[SimShell.scala 92:23]
  wire  mod_mem_io_axi_ar_valid; // @[SimShell.scala 92:23]
  wire [31:0] mod_mem_io_axi_ar_bits_addr; // @[SimShell.scala 92:23]
  wire [7:0] mod_mem_io_axi_ar_bits_len; // @[SimShell.scala 92:23]
  wire  mod_mem_io_axi_r_ready; // @[SimShell.scala 92:23]
  wire  mod_mem_io_axi_r_valid; // @[SimShell.scala 92:23]
  wire [63:0] mod_mem_io_axi_r_bits_data; // @[SimShell.scala 92:23]
  wire  mod_mem_io_axi_r_bits_last; // @[SimShell.scala 92:23]
  VTASim mod_sim ( // @[SimShell.scala 90:23]
    .clock(mod_sim_clock),
    .reset(mod_sim_reset),
    .sim_wait(mod_sim_sim_wait)
  );
  VTAHost mod_host ( // @[SimShell.scala 91:24]
    .clock(mod_host_clock),
    .reset(mod_host_reset),
    .io_axi_aw_ready(mod_host_io_axi_aw_ready),
    .io_axi_aw_valid(mod_host_io_axi_aw_valid),
    .io_axi_aw_bits_addr(mod_host_io_axi_aw_bits_addr),
    .io_axi_w_ready(mod_host_io_axi_w_ready),
    .io_axi_w_valid(mod_host_io_axi_w_valid),
    .io_axi_w_bits_data(mod_host_io_axi_w_bits_data),
    .io_axi_b_ready(mod_host_io_axi_b_ready),
    .io_axi_b_valid(mod_host_io_axi_b_valid),
    .io_axi_ar_ready(mod_host_io_axi_ar_ready),
    .io_axi_ar_valid(mod_host_io_axi_ar_valid),
    .io_axi_ar_bits_addr(mod_host_io_axi_ar_bits_addr),
    .io_axi_r_ready(mod_host_io_axi_r_ready),
    .io_axi_r_valid(mod_host_io_axi_r_valid),
    .io_axi_r_bits_data(mod_host_io_axi_r_bits_data)
  );
  VTAMem mod_mem ( // @[SimShell.scala 92:23]
    .clock(mod_mem_clock),
    .reset(mod_mem_reset),
    .io_axi_aw_ready(mod_mem_io_axi_aw_ready),
    .io_axi_aw_valid(mod_mem_io_axi_aw_valid),
    .io_axi_aw_bits_addr(mod_mem_io_axi_aw_bits_addr),
    .io_axi_aw_bits_len(mod_mem_io_axi_aw_bits_len),
    .io_axi_w_ready(mod_mem_io_axi_w_ready),
    .io_axi_w_valid(mod_mem_io_axi_w_valid),
    .io_axi_w_bits_data(mod_mem_io_axi_w_bits_data),
    .io_axi_w_bits_last(mod_mem_io_axi_w_bits_last),
    .io_axi_b_ready(mod_mem_io_axi_b_ready),
    .io_axi_b_valid(mod_mem_io_axi_b_valid),
    .io_axi_ar_ready(mod_mem_io_axi_ar_ready),
    .io_axi_ar_valid(mod_mem_io_axi_ar_valid),
    .io_axi_ar_bits_addr(mod_mem_io_axi_ar_bits_addr),
    .io_axi_ar_bits_len(mod_mem_io_axi_ar_bits_len),
    .io_axi_r_ready(mod_mem_io_axi_r_ready),
    .io_axi_r_valid(mod_mem_io_axi_r_valid),
    .io_axi_r_bits_data(mod_mem_io_axi_r_bits_data),
    .io_axi_r_bits_last(mod_mem_io_axi_r_bits_last)
  );
  assign mem_aw_ready = mod_mem_io_axi_aw_ready; // @[SimShell.scala 93:7]
  assign mem_w_ready = mod_mem_io_axi_w_ready; // @[SimShell.scala 93:7]
  assign mem_b_valid = mod_mem_io_axi_b_valid; // @[SimShell.scala 93:7]
  assign mem_ar_ready = mod_mem_io_axi_ar_ready; // @[SimShell.scala 93:7]
  assign mem_r_valid = mod_mem_io_axi_r_valid; // @[SimShell.scala 93:7]
  assign mem_r_bits_data = mod_mem_io_axi_r_bits_data; // @[SimShell.scala 93:7]
  assign mem_r_bits_last = mod_mem_io_axi_r_bits_last; // @[SimShell.scala 93:7]
  assign host_aw_valid = mod_host_io_axi_aw_valid; // @[SimShell.scala 94:8]
  assign host_aw_bits_addr = mod_host_io_axi_aw_bits_addr; // @[SimShell.scala 94:8]
  assign host_w_valid = mod_host_io_axi_w_valid; // @[SimShell.scala 94:8]
  assign host_w_bits_data = mod_host_io_axi_w_bits_data; // @[SimShell.scala 94:8]
  assign host_b_ready = mod_host_io_axi_b_ready; // @[SimShell.scala 94:8]
  assign host_ar_valid = mod_host_io_axi_ar_valid; // @[SimShell.scala 94:8]
  assign host_ar_bits_addr = mod_host_io_axi_ar_bits_addr; // @[SimShell.scala 94:8]
  assign host_r_ready = mod_host_io_axi_r_ready; // @[SimShell.scala 94:8]
  assign sim_wait = mod_sim_sim_wait; // @[SimShell.scala 97:12]
  assign mod_sim_clock = sim_clock; // @[SimShell.scala 96:17]
  assign mod_sim_reset = reset; // @[SimShell.scala 95:17]
  assign mod_host_clock = clock;
  assign mod_host_reset = reset;
  assign mod_host_io_axi_aw_ready = host_aw_ready; // @[SimShell.scala 94:8]
  assign mod_host_io_axi_w_ready = host_w_ready; // @[SimShell.scala 94:8]
  assign mod_host_io_axi_b_valid = host_b_valid; // @[SimShell.scala 94:8]
  assign mod_host_io_axi_ar_ready = host_ar_ready; // @[SimShell.scala 94:8]
  assign mod_host_io_axi_r_valid = host_r_valid; // @[SimShell.scala 94:8]
  assign mod_host_io_axi_r_bits_data = host_r_bits_data; // @[SimShell.scala 94:8]
  assign mod_mem_clock = clock;
  assign mod_mem_reset = reset;
  assign mod_mem_io_axi_aw_valid = mem_aw_valid; // @[SimShell.scala 93:7]
  assign mod_mem_io_axi_aw_bits_addr = mem_aw_bits_addr; // @[SimShell.scala 93:7]
  assign mod_mem_io_axi_aw_bits_len = mem_aw_bits_len; // @[SimShell.scala 93:7]
  assign mod_mem_io_axi_w_valid = mem_w_valid; // @[SimShell.scala 93:7]
  assign mod_mem_io_axi_w_bits_data = mem_w_bits_data; // @[SimShell.scala 93:7]
  assign mod_mem_io_axi_w_bits_last = mem_w_bits_last; // @[SimShell.scala 93:7]
  assign mod_mem_io_axi_b_ready = mem_b_ready; // @[SimShell.scala 93:7]
  assign mod_mem_io_axi_ar_valid = mem_ar_valid; // @[SimShell.scala 93:7]
  assign mod_mem_io_axi_ar_bits_addr = mem_ar_bits_addr; // @[SimShell.scala 93:7]
  assign mod_mem_io_axi_ar_bits_len = mem_ar_bits_len; // @[SimShell.scala 93:7]
  assign mod_mem_io_axi_r_ready = mem_r_ready; // @[SimShell.scala 93:7]
endmodule
module VCR(
  input         clock,
  input         reset,
  output        io_host_aw_ready,
  input         io_host_aw_valid,
  input  [31:0] io_host_aw_bits_addr,
  output        io_host_w_ready,
  input         io_host_w_valid,
  input  [31:0] io_host_w_bits_data,
  input         io_host_b_ready,
  output        io_host_b_valid,
  output        io_host_ar_ready,
  input         io_host_ar_valid,
  input  [31:0] io_host_ar_bits_addr,
  input         io_host_r_ready,
  output        io_host_r_valid,
  output [31:0] io_host_r_bits_data,
  output        io_vcr_launch,
  input         io_vcr_finish,
  input         io_vcr_ecnt_0_valid,
  input  [31:0] io_vcr_ecnt_0_bits,
  output [31:0] io_vcr_vals_0,
  output [31:0] io_vcr_vals_1,
  output [31:0] io_vcr_ptrs_0,
  output [31:0] io_vcr_ptrs_2
);
  reg [31:0] waddr; // @[VCR.scala 92:22]
  reg [31:0] _RAND_0;
  reg [1:0] wstate; // @[VCR.scala 95:23]
  reg [31:0] _RAND_1;
  reg  rstate; // @[VCR.scala 99:23]
  reg [31:0] _RAND_2;
  reg [31:0] rdata; // @[VCR.scala 100:22]
  reg [31:0] _RAND_3;
  reg [31:0] reg_0; // @[VCR.scala 106:37]
  reg [31:0] _RAND_4;
  reg [31:0] reg_1; // @[VCR.scala 106:37]
  reg [31:0] _RAND_5;
  reg [31:0] reg_2; // @[VCR.scala 106:37]
  reg [31:0] _RAND_6;
  reg [31:0] reg_3; // @[VCR.scala 106:37]
  reg [31:0] _RAND_7;
  reg [31:0] reg_4; // @[VCR.scala 106:37]
  reg [31:0] _RAND_8;
  reg [31:0] reg_5; // @[VCR.scala 106:37]
  reg [31:0] _RAND_9;
  reg [31:0] reg_6; // @[VCR.scala 106:37]
  reg [31:0] _RAND_10;
  reg [31:0] reg_7; // @[VCR.scala 106:37]
  reg [31:0] _RAND_11;
  wire  _T; // @[Conditional.scala 37:30]
  wire  _T_1; // @[Conditional.scala 37:30]
  wire  _T_2; // @[Conditional.scala 37:30]
  wire  _T_3; // @[Decoupled.scala 40:37]
  wire  _T_7; // @[Conditional.scala 37:30]
  wire  _GEN_7; // @[VCR.scala 141:31]
  wire  _T_11; // @[Decoupled.scala 40:37]
  wire  _T_12; // @[VCR.scala 159:46]
  wire  _T_13; // @[VCR.scala 159:33]
  wire  _T_15; // @[VCR.scala 166:53]
  wire  _T_16; // @[VCR.scala 166:35]
  wire  _T_18; // @[VCR.scala 172:46]
  wire  _T_19; // @[VCR.scala 172:28]
  wire  _T_21; // @[VCR.scala 172:46]
  wire  _T_22; // @[VCR.scala 172:28]
  wire  _T_24; // @[VCR.scala 172:46]
  wire  _T_25; // @[VCR.scala 172:28]
  wire  _T_27; // @[VCR.scala 172:46]
  wire  _T_28; // @[VCR.scala 172:28]
  wire  _T_30; // @[VCR.scala 172:46]
  wire  _T_31; // @[VCR.scala 172:28]
  wire  _T_33; // @[VCR.scala 172:46]
  wire  _T_34; // @[VCR.scala 172:28]
  wire  _T_35; // @[Decoupled.scala 40:37]
  wire  _T_36; // @[Mux.scala 68:19]
  wire  _T_38; // @[Mux.scala 68:19]
  wire  _T_40; // @[Mux.scala 68:19]
  wire  _T_42; // @[Mux.scala 68:19]
  wire  _T_44; // @[Mux.scala 68:19]
  wire  _T_46; // @[Mux.scala 68:19]
  wire  _T_48; // @[Mux.scala 68:19]
  wire  _T_50; // @[Mux.scala 68:19]
  assign _T = 2'h0 == wstate; // @[Conditional.scala 37:30]
  assign _T_1 = 2'h1 == wstate; // @[Conditional.scala 37:30]
  assign _T_2 = 2'h2 == wstate; // @[Conditional.scala 37:30]
  assign _T_3 = io_host_aw_ready & io_host_aw_valid; // @[Decoupled.scala 40:37]
  assign _T_7 = 1'h0 == rstate; // @[Conditional.scala 37:30]
  assign _GEN_7 = io_host_ar_valid | rstate; // @[VCR.scala 141:31]
  assign _T_11 = io_host_w_ready & io_host_w_valid; // @[Decoupled.scala 40:37]
  assign _T_12 = 32'h0 == waddr; // @[VCR.scala 159:46]
  assign _T_13 = _T_11 & _T_12; // @[VCR.scala 159:33]
  assign _T_15 = 32'h4 == waddr; // @[VCR.scala 166:53]
  assign _T_16 = _T_11 & _T_15; // @[VCR.scala 166:35]
  assign _T_18 = 32'h8 == waddr; // @[VCR.scala 172:46]
  assign _T_19 = _T_11 & _T_18; // @[VCR.scala 172:28]
  assign _T_21 = 32'hc == waddr; // @[VCR.scala 172:46]
  assign _T_22 = _T_11 & _T_21; // @[VCR.scala 172:28]
  assign _T_24 = 32'h10 == waddr; // @[VCR.scala 172:46]
  assign _T_25 = _T_11 & _T_24; // @[VCR.scala 172:28]
  assign _T_27 = 32'h14 == waddr; // @[VCR.scala 172:46]
  assign _T_28 = _T_11 & _T_27; // @[VCR.scala 172:28]
  assign _T_30 = 32'h18 == waddr; // @[VCR.scala 172:46]
  assign _T_31 = _T_11 & _T_30; // @[VCR.scala 172:28]
  assign _T_33 = 32'h1c == waddr; // @[VCR.scala 172:46]
  assign _T_34 = _T_11 & _T_33; // @[VCR.scala 172:28]
  assign _T_35 = io_host_ar_ready & io_host_ar_valid; // @[Decoupled.scala 40:37]
  assign _T_36 = 32'h1c == io_host_ar_bits_addr; // @[Mux.scala 68:19]
  assign _T_38 = 32'h18 == io_host_ar_bits_addr; // @[Mux.scala 68:19]
  assign _T_40 = 32'h14 == io_host_ar_bits_addr; // @[Mux.scala 68:19]
  assign _T_42 = 32'h10 == io_host_ar_bits_addr; // @[Mux.scala 68:19]
  assign _T_44 = 32'hc == io_host_ar_bits_addr; // @[Mux.scala 68:19]
  assign _T_46 = 32'h8 == io_host_ar_bits_addr; // @[Mux.scala 68:19]
  assign _T_48 = 32'h4 == io_host_ar_bits_addr; // @[Mux.scala 68:19]
  assign _T_50 = 32'h0 == io_host_ar_bits_addr; // @[Mux.scala 68:19]
  assign io_host_aw_ready = wstate == 2'h0; // @[VCR.scala 133:20]
  assign io_host_w_ready = wstate == 2'h1; // @[VCR.scala 134:19]
  assign io_host_b_valid = wstate == 2'h2; // @[VCR.scala 135:19]
  assign io_host_ar_ready = rstate == 1'h0; // @[VCR.scala 152:20]
  assign io_host_r_valid = rstate; // @[VCR.scala 153:19]
  assign io_host_r_bits_data = rdata; // @[VCR.scala 154:23]
  assign io_vcr_launch = reg_0[0]; // @[VCR.scala 181:17]
  assign io_vcr_vals_0 = reg_2; // @[VCR.scala 184:20]
  assign io_vcr_vals_1 = reg_3; // @[VCR.scala 184:20]
  assign io_vcr_ptrs_0 = reg_4; // @[VCR.scala 189:22]
  assign io_vcr_ptrs_2 = reg_6; // @[VCR.scala 189:22]
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  waddr = _RAND_0[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  wstate = _RAND_1[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  rstate = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  rdata = _RAND_3[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  reg_0 = _RAND_4[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  reg_1 = _RAND_5[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  reg_2 = _RAND_6[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  reg_3 = _RAND_7[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_8 = {1{`RANDOM}};
  reg_4 = _RAND_8[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_9 = {1{`RANDOM}};
  reg_5 = _RAND_9[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_10 = {1{`RANDOM}};
  reg_6 = _RAND_10[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_11 = {1{`RANDOM}};
  reg_7 = _RAND_11[31:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end
  always @(posedge clock) begin
    if (reset) begin
      waddr <= 32'hffff;
    end else begin
      if (_T_3) begin
        waddr <= io_host_aw_bits_addr;
      end
    end
    if (reset) begin
      wstate <= 2'h0;
    end else begin
      if (_T) begin
        if (io_host_aw_valid) begin
          wstate <= 2'h1;
        end
      end else begin
        if (_T_1) begin
          if (io_host_w_valid) begin
            wstate <= 2'h2;
          end
        end else begin
          if (_T_2) begin
            if (io_host_b_ready) begin
              wstate <= 2'h0;
            end
          end
        end
      end
    end
    if (reset) begin
      rstate <= 1'h0;
    end else begin
      if (_T_7) begin
        rstate <= _GEN_7;
      end else begin
        if (rstate) begin
          if (io_host_r_ready) begin
            rstate <= 1'h0;
          end
        end
      end
    end
    if (reset) begin
      rdata <= 32'h0;
    end else begin
      if (_T_35) begin
        if (_T_50) begin
          rdata <= reg_0;
        end else begin
          if (_T_48) begin
            rdata <= reg_1;
          end else begin
            if (_T_46) begin
              rdata <= reg_2;
            end else begin
              if (_T_44) begin
                rdata <= reg_3;
              end else begin
                if (_T_42) begin
                  rdata <= reg_4;
                end else begin
                  if (_T_40) begin
                    rdata <= reg_5;
                  end else begin
                    if (_T_38) begin
                      rdata <= reg_6;
                    end else begin
                      if (_T_36) begin
                        rdata <= reg_7;
                      end else begin
                        rdata <= 32'h0;
                      end
                    end
                  end
                end
              end
            end
          end
        end
      end
    end
    if (reset) begin
      reg_0 <= 32'h0;
    end else begin
      if (io_vcr_finish) begin
        reg_0 <= 32'h2;
      end else begin
        if (_T_13) begin
          reg_0 <= io_host_w_bits_data;
        end
      end
    end
    if (reset) begin
      reg_1 <= 32'h0;
    end else begin
      if (io_vcr_ecnt_0_valid) begin
        reg_1 <= io_vcr_ecnt_0_bits;
      end else begin
        if (_T_16) begin
          reg_1 <= io_host_w_bits_data;
        end
      end
    end
    if (reset) begin
      reg_2 <= 32'h0;
    end else begin
      if (_T_19) begin
        reg_2 <= io_host_w_bits_data;
      end
    end
    if (reset) begin
      reg_3 <= 32'h0;
    end else begin
      if (_T_22) begin
        reg_3 <= io_host_w_bits_data;
      end
    end
    if (reset) begin
      reg_4 <= 32'h0;
    end else begin
      if (_T_25) begin
        reg_4 <= io_host_w_bits_data;
      end
    end
    if (reset) begin
      reg_5 <= 32'h0;
    end else begin
      if (_T_28) begin
        reg_5 <= io_host_w_bits_data;
      end
    end
    if (reset) begin
      reg_6 <= 32'h0;
    end else begin
      if (_T_31) begin
        reg_6 <= io_host_w_bits_data;
      end
    end
    if (reset) begin
      reg_7 <= 32'h0;
    end else begin
      if (_T_34) begin
        reg_7 <= io_host_w_bits_data;
      end
    end
  end
endmodule
module Arbiter(
  output        io_in_0_ready,
  input         io_in_0_valid,
  input  [31:0] io_in_0_bits_addr,
  input  [7:0]  io_in_0_bits_len,
  input         io_out_ready,
  output        io_out_valid,
  output [31:0] io_out_bits_addr,
  output [7:0]  io_out_bits_len
);
  assign io_in_0_ready = io_out_ready; // @[Arbiter.scala 134:14]
  assign io_out_valid = io_in_0_valid; // @[Arbiter.scala 135:16]
  assign io_out_bits_addr = io_in_0_bits_addr; // @[Arbiter.scala 124:15]
  assign io_out_bits_len = io_in_0_bits_len; // @[Arbiter.scala 124:15]
endmodule
module VME(
  input         clock,
  input         reset,
  input         io_mem_aw_ready,
  output        io_mem_aw_valid,
  output [31:0] io_mem_aw_bits_addr,
  output [7:0]  io_mem_aw_bits_len,
  input         io_mem_w_ready,
  output        io_mem_w_valid,
  output [63:0] io_mem_w_bits_data,
  output        io_mem_w_bits_last,
  output        io_mem_b_ready,
  input         io_mem_b_valid,
  input         io_mem_ar_ready,
  output        io_mem_ar_valid,
  output [31:0] io_mem_ar_bits_addr,
  output [7:0]  io_mem_ar_bits_len,
  output        io_mem_r_ready,
  input         io_mem_r_valid,
  input  [63:0] io_mem_r_bits_data,
  input         io_mem_r_bits_last,
  output        io_vme_rd_0_cmd_ready,
  input         io_vme_rd_0_cmd_valid,
  input  [31:0] io_vme_rd_0_cmd_bits_addr,
  input  [7:0]  io_vme_rd_0_cmd_bits_len,
  input         io_vme_rd_0_data_ready,
  output        io_vme_rd_0_data_valid,
  output [63:0] io_vme_rd_0_data_bits,
  output        io_vme_wr_0_cmd_ready,
  input         io_vme_wr_0_cmd_valid,
  input  [31:0] io_vme_wr_0_cmd_bits_addr,
  input  [7:0]  io_vme_wr_0_cmd_bits_len,
  output        io_vme_wr_0_data_ready,
  input         io_vme_wr_0_data_valid,
  input  [63:0] io_vme_wr_0_data_bits,
  output        io_vme_wr_0_ack
);
  wire  rd_arb_io_in_0_ready; // @[VME.scala 143:22]
  wire  rd_arb_io_in_0_valid; // @[VME.scala 143:22]
  wire [31:0] rd_arb_io_in_0_bits_addr; // @[VME.scala 143:22]
  wire [7:0] rd_arb_io_in_0_bits_len; // @[VME.scala 143:22]
  wire  rd_arb_io_out_ready; // @[VME.scala 143:22]
  wire  rd_arb_io_out_valid; // @[VME.scala 143:22]
  wire [31:0] rd_arb_io_out_bits_addr; // @[VME.scala 143:22]
  wire [7:0] rd_arb_io_out_bits_len; // @[VME.scala 143:22]
  wire  _T; // @[Decoupled.scala 40:37]
  reg [1:0] rstate; // @[VME.scala 149:23]
  reg [31:0] _RAND_0;
  wire  _T_1; // @[Conditional.scala 37:30]
  wire  _T_2; // @[Conditional.scala 37:30]
  wire  _T_3; // @[Conditional.scala 37:30]
  wire  _T_4; // @[Decoupled.scala 40:37]
  wire  _T_5; // @[VME.scala 163:29]
  reg [1:0] wstate; // @[VME.scala 170:23]
  reg [31:0] _RAND_1;
  reg [7:0] wr_cnt; // @[VME.scala 173:23]
  reg [31:0] _RAND_2;
  wire  _T_6; // @[VME.scala 175:16]
  wire  _T_7; // @[Decoupled.scala 40:37]
  wire [7:0] _T_9; // @[VME.scala 178:22]
  wire  _T_10; // @[Conditional.scala 37:30]
  wire  _T_11; // @[Conditional.scala 37:30]
  wire  _T_12; // @[Conditional.scala 37:30]
  wire  _T_13; // @[VME.scala 193:37]
  wire  _T_14; // @[VME.scala 193:65]
  wire  _T_15; // @[VME.scala 193:55]
  wire  _T_16; // @[Conditional.scala 37:30]
  reg [7:0] rd_len; // @[VME.scala 206:23]
  reg [31:0] _RAND_3;
  reg [7:0] wr_len; // @[VME.scala 207:23]
  reg [31:0] _RAND_4;
  reg [31:0] rd_addr; // @[VME.scala 208:24]
  reg [31:0] _RAND_5;
  reg [31:0] wr_addr; // @[VME.scala 209:24]
  reg [31:0] _RAND_6;
  wire  _T_18; // @[Decoupled.scala 40:37]
  wire  _T_24; // @[VME.scala 232:37]
  wire  _T_32; // @[VME.scala 249:28]
  Arbiter rd_arb ( // @[VME.scala 143:22]
    .io_in_0_ready(rd_arb_io_in_0_ready),
    .io_in_0_valid(rd_arb_io_in_0_valid),
    .io_in_0_bits_addr(rd_arb_io_in_0_bits_addr),
    .io_in_0_bits_len(rd_arb_io_in_0_bits_len),
    .io_out_ready(rd_arb_io_out_ready),
    .io_out_valid(rd_arb_io_out_valid),
    .io_out_bits_addr(rd_arb_io_out_bits_addr),
    .io_out_bits_len(rd_arb_io_out_bits_len)
  );
  assign _T = rd_arb_io_out_ready & rd_arb_io_out_valid; // @[Decoupled.scala 40:37]
  assign _T_1 = 2'h0 == rstate; // @[Conditional.scala 37:30]
  assign _T_2 = 2'h1 == rstate; // @[Conditional.scala 37:30]
  assign _T_3 = 2'h2 == rstate; // @[Conditional.scala 37:30]
  assign _T_4 = io_mem_r_ready & io_mem_r_valid; // @[Decoupled.scala 40:37]
  assign _T_5 = _T_4 & io_mem_r_bits_last; // @[VME.scala 163:29]
  assign _T_6 = wstate == 2'h0; // @[VME.scala 175:16]
  assign _T_7 = io_mem_w_ready & io_mem_w_valid; // @[Decoupled.scala 40:37]
  assign _T_9 = wr_cnt + 8'h1; // @[VME.scala 178:22]
  assign _T_10 = 2'h0 == wstate; // @[Conditional.scala 37:30]
  assign _T_11 = 2'h1 == wstate; // @[Conditional.scala 37:30]
  assign _T_12 = 2'h2 == wstate; // @[Conditional.scala 37:30]
  assign _T_13 = io_vme_wr_0_data_valid & io_mem_w_ready; // @[VME.scala 193:37]
  assign _T_14 = wr_cnt == io_vme_wr_0_cmd_bits_len; // @[VME.scala 193:65]
  assign _T_15 = _T_13 & _T_14; // @[VME.scala 193:55]
  assign _T_16 = 2'h3 == wstate; // @[Conditional.scala 37:30]
  assign _T_18 = io_vme_wr_0_cmd_ready & io_vme_wr_0_cmd_valid; // @[Decoupled.scala 40:37]
  assign _T_24 = wstate == 2'h2; // @[VME.scala 232:37]
  assign _T_32 = rstate == 2'h2; // @[VME.scala 249:28]
  assign io_mem_aw_valid = wstate == 2'h1; // @[VME.scala 235:19]
  assign io_mem_aw_bits_addr = wr_addr; // @[VME.scala 236:23]
  assign io_mem_aw_bits_len = wr_len; // @[VME.scala 237:22]
  assign io_mem_w_valid = _T_24 & io_vme_wr_0_data_valid; // @[VME.scala 239:18]
  assign io_mem_w_bits_data = io_vme_wr_0_data_bits; // @[VME.scala 240:22]
  assign io_mem_w_bits_last = wr_cnt == io_vme_wr_0_cmd_bits_len; // @[VME.scala 241:22]
  assign io_mem_b_ready = wstate == 2'h3; // @[VME.scala 243:18]
  assign io_mem_ar_valid = rstate == 2'h1; // @[VME.scala 245:19]
  assign io_mem_ar_bits_addr = rd_addr; // @[VME.scala 246:23]
  assign io_mem_ar_bits_len = rd_len; // @[VME.scala 247:22]
  assign io_mem_r_ready = _T_32 & io_vme_rd_0_data_ready; // @[VME.scala 249:18]
  assign io_vme_rd_0_cmd_ready = rd_arb_io_in_0_ready; // @[VME.scala 146:53]
  assign io_vme_rd_0_data_valid = io_mem_r_valid; // @[VME.scala 226:29]
  assign io_vme_rd_0_data_bits = io_mem_r_bits_data; // @[VME.scala 227:28]
  assign io_vme_wr_0_cmd_ready = wstate == 2'h0; // @[VME.scala 230:26]
  assign io_vme_wr_0_data_ready = _T_24 & io_mem_w_ready; // @[VME.scala 232:27]
  assign io_vme_wr_0_ack = io_mem_b_ready & io_mem_b_valid; // @[VME.scala 231:20]
  assign rd_arb_io_in_0_valid = io_vme_rd_0_cmd_valid; // @[VME.scala 146:53]
  assign rd_arb_io_in_0_bits_addr = io_vme_rd_0_cmd_bits_addr; // @[VME.scala 146:53]
  assign rd_arb_io_in_0_bits_len = io_vme_rd_0_cmd_bits_len; // @[VME.scala 146:53]
  assign rd_arb_io_out_ready = rstate == 2'h0; // @[VME.scala 222:23]
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  rstate = _RAND_0[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  wstate = _RAND_1[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  wr_cnt = _RAND_2[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  rd_len = _RAND_3[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  wr_len = _RAND_4[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  rd_addr = _RAND_5[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  wr_addr = _RAND_6[31:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end
  always @(posedge clock) begin
    if (reset) begin
      rstate <= 2'h0;
    end else begin
      if (_T_1) begin
        if (rd_arb_io_out_valid) begin
          rstate <= 2'h1;
        end
      end else begin
        if (_T_2) begin
          if (io_mem_ar_ready) begin
            rstate <= 2'h2;
          end
        end else begin
          if (_T_3) begin
            if (_T_5) begin
              rstate <= 2'h0;
            end
          end
        end
      end
    end
    if (reset) begin
      wstate <= 2'h0;
    end else begin
      if (_T_10) begin
        if (io_vme_wr_0_cmd_valid) begin
          wstate <= 2'h1;
        end
      end else begin
        if (_T_11) begin
          if (io_mem_aw_ready) begin
            wstate <= 2'h2;
          end
        end else begin
          if (_T_12) begin
            if (_T_15) begin
              wstate <= 2'h3;
            end
          end else begin
            if (_T_16) begin
              if (io_mem_b_valid) begin
                wstate <= 2'h0;
              end
            end
          end
        end
      end
    end
    if (reset) begin
      wr_cnt <= 8'h0;
    end else begin
      if (_T_6) begin
        wr_cnt <= 8'h0;
      end else begin
        if (_T_7) begin
          wr_cnt <= _T_9;
        end
      end
    end
    if (reset) begin
      rd_len <= 8'h0;
    end else begin
      if (_T) begin
        rd_len <= rd_arb_io_out_bits_len;
      end
    end
    if (reset) begin
      wr_len <= 8'h0;
    end else begin
      if (_T_18) begin
        wr_len <= io_vme_wr_0_cmd_bits_len;
      end
    end
    if (reset) begin
      rd_addr <= 32'h0;
    end else begin
      if (_T) begin
        rd_addr <= rd_arb_io_out_bits_addr;
      end
    end
    if (reset) begin
      wr_addr <= 32'h0;
    end else begin
      if (_T_18) begin
        wr_addr <= io_vme_wr_0_cmd_bits_addr;
      end
    end
  end
endmodule
module Queue(
  input         clock,
  input         reset,
  output        io_enq_ready,
  input         io_enq_valid,
  input  [63:0] io_enq_bits,
  input         io_deq_ready,
  output        io_deq_valid,
  output [63:0] io_deq_bits
);
  reg [63:0] _T [0:39]; // @[Decoupled.scala 208:24]
  reg [63:0] _RAND_0;
  wire [63:0] _T__T_18_data; // @[Decoupled.scala 208:24]
  wire [5:0] _T__T_18_addr; // @[Decoupled.scala 208:24]
  reg [63:0] _RAND_1;
  wire [63:0] _T__T_10_data; // @[Decoupled.scala 208:24]
  wire [5:0] _T__T_10_addr; // @[Decoupled.scala 208:24]
  wire  _T__T_10_mask; // @[Decoupled.scala 208:24]
  wire  _T__T_10_en; // @[Decoupled.scala 208:24]
  reg [5:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_2;
  reg [5:0] value_1; // @[Counter.scala 29:33]
  reg [31:0] _RAND_3;
  reg  _T_1; // @[Decoupled.scala 211:35]
  reg [31:0] _RAND_4;
  wire  _T_2; // @[Decoupled.scala 213:41]
  wire  _T_3; // @[Decoupled.scala 214:36]
  wire  _T_4; // @[Decoupled.scala 214:33]
  wire  _T_5; // @[Decoupled.scala 215:32]
  wire  _T_6; // @[Decoupled.scala 40:37]
  wire  _T_8; // @[Decoupled.scala 40:37]
  wire  wrap; // @[Counter.scala 37:24]
  wire [5:0] _T_12; // @[Counter.scala 38:22]
  wire  wrap_1; // @[Counter.scala 37:24]
  wire [5:0] _T_14; // @[Counter.scala 38:22]
  wire  _T_15; // @[Decoupled.scala 226:16]
  assign _T__T_18_addr = value_1;
  `ifndef RANDOMIZE_GARBAGE_ASSIGN
  assign _T__T_18_data = _T[_T__T_18_addr]; // @[Decoupled.scala 208:24]
  `else
  assign _T__T_18_data = _T__T_18_addr >= 6'h28 ? _RAND_1[63:0] : _T[_T__T_18_addr]; // @[Decoupled.scala 208:24]
  `endif // RANDOMIZE_GARBAGE_ASSIGN
  assign _T__T_10_data = io_enq_bits;
  assign _T__T_10_addr = value;
  assign _T__T_10_mask = 1'h1;
  assign _T__T_10_en = io_enq_ready & io_enq_valid;
  assign _T_2 = value == value_1; // @[Decoupled.scala 213:41]
  assign _T_3 = _T_1 == 1'h0; // @[Decoupled.scala 214:36]
  assign _T_4 = _T_2 & _T_3; // @[Decoupled.scala 214:33]
  assign _T_5 = _T_2 & _T_1; // @[Decoupled.scala 215:32]
  assign _T_6 = io_enq_ready & io_enq_valid; // @[Decoupled.scala 40:37]
  assign _T_8 = io_deq_ready & io_deq_valid; // @[Decoupled.scala 40:37]
  assign wrap = value == 6'h27; // @[Counter.scala 37:24]
  assign _T_12 = value + 6'h1; // @[Counter.scala 38:22]
  assign wrap_1 = value_1 == 6'h27; // @[Counter.scala 37:24]
  assign _T_14 = value_1 + 6'h1; // @[Counter.scala 38:22]
  assign _T_15 = _T_6 != _T_8; // @[Decoupled.scala 226:16]
  assign io_enq_ready = _T_5 == 1'h0; // @[Decoupled.scala 231:16]
  assign io_deq_valid = _T_4 == 1'h0; // @[Decoupled.scala 230:16]
  assign io_deq_bits = _T__T_18_data; // @[Decoupled.scala 232:15]
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
  _RAND_0 = {2{`RANDOM}};
  `ifdef RANDOMIZE_MEM_INIT
  for (initvar = 0; initvar < 40; initvar = initvar+1)
    _T[initvar] = _RAND_0[63:0];
  `endif // RANDOMIZE_MEM_INIT
  _RAND_1 = {2{`RANDOM}};
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  value = _RAND_2[5:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  value_1 = _RAND_3[5:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_1 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end
  always @(posedge clock) begin
    if(_T__T_10_en & _T__T_10_mask) begin
      _T[_T__T_10_addr] <= _T__T_10_data; // @[Decoupled.scala 208:24]
    end
    if (reset) begin
      value <= 6'h0;
    end else begin
      if (_T_6) begin
        if (wrap) begin
          value <= 6'h0;
        end else begin
          value <= _T_12;
        end
      end
    end
    if (reset) begin
      value_1 <= 6'h0;
    end else begin
      if (_T_8) begin
        if (wrap_1) begin
          value_1 <= 6'h0;
        end else begin
          value_1 <= _T_14;
        end
      end
    end
    if (reset) begin
      _T_1 <= 1'h0;
    end else begin
      if (_T_15) begin
        _T_1 <= _T_6;
      end
    end
  end
endmodule
module VTAShell2(
  input         clock,
  input         reset,
  output        io_host_aw_ready,
  input         io_host_aw_valid,
  input  [31:0] io_host_aw_bits_addr,
  output        io_host_w_ready,
  input         io_host_w_valid,
  input  [31:0] io_host_w_bits_data,
  input         io_host_b_ready,
  output        io_host_b_valid,
  output        io_host_ar_ready,
  input         io_host_ar_valid,
  input  [31:0] io_host_ar_bits_addr,
  input         io_host_r_ready,
  output        io_host_r_valid,
  output [31:0] io_host_r_bits_data,
  input         io_mem_aw_ready,
  output        io_mem_aw_valid,
  output [31:0] io_mem_aw_bits_addr,
  output [7:0]  io_mem_aw_bits_len,
  input         io_mem_w_ready,
  output        io_mem_w_valid,
  output [63:0] io_mem_w_bits_data,
  output        io_mem_w_bits_last,
  output        io_mem_b_ready,
  input         io_mem_b_valid,
  input         io_mem_ar_ready,
  output        io_mem_ar_valid,
  output [31:0] io_mem_ar_bits_addr,
  output [7:0]  io_mem_ar_bits_len,
  output        io_mem_r_ready,
  input         io_mem_r_valid,
  input  [63:0] io_mem_r_bits_data,
  input         io_mem_r_bits_last
);
  wire  vcr_clock; // @[VCRAccel.scala 98:19]
  wire  vcr_reset; // @[VCRAccel.scala 98:19]
  wire  vcr_io_host_aw_ready; // @[VCRAccel.scala 98:19]
  wire  vcr_io_host_aw_valid; // @[VCRAccel.scala 98:19]
  wire [31:0] vcr_io_host_aw_bits_addr; // @[VCRAccel.scala 98:19]
  wire  vcr_io_host_w_ready; // @[VCRAccel.scala 98:19]
  wire  vcr_io_host_w_valid; // @[VCRAccel.scala 98:19]
  wire [31:0] vcr_io_host_w_bits_data; // @[VCRAccel.scala 98:19]
  wire  vcr_io_host_b_ready; // @[VCRAccel.scala 98:19]
  wire  vcr_io_host_b_valid; // @[VCRAccel.scala 98:19]
  wire  vcr_io_host_ar_ready; // @[VCRAccel.scala 98:19]
  wire  vcr_io_host_ar_valid; // @[VCRAccel.scala 98:19]
  wire [31:0] vcr_io_host_ar_bits_addr; // @[VCRAccel.scala 98:19]
  wire  vcr_io_host_r_ready; // @[VCRAccel.scala 98:19]
  wire  vcr_io_host_r_valid; // @[VCRAccel.scala 98:19]
  wire [31:0] vcr_io_host_r_bits_data; // @[VCRAccel.scala 98:19]
  wire  vcr_io_vcr_launch; // @[VCRAccel.scala 98:19]
  wire  vcr_io_vcr_finish; // @[VCRAccel.scala 98:19]
  wire  vcr_io_vcr_ecnt_0_valid; // @[VCRAccel.scala 98:19]
  wire [31:0] vcr_io_vcr_ecnt_0_bits; // @[VCRAccel.scala 98:19]
  wire [31:0] vcr_io_vcr_vals_0; // @[VCRAccel.scala 98:19]
  wire [31:0] vcr_io_vcr_vals_1; // @[VCRAccel.scala 98:19]
  wire [31:0] vcr_io_vcr_ptrs_0; // @[VCRAccel.scala 98:19]
  wire [31:0] vcr_io_vcr_ptrs_2; // @[VCRAccel.scala 98:19]
  wire  vmem_clock; // @[VCRAccel.scala 99:20]
  wire  vmem_reset; // @[VCRAccel.scala 99:20]
  wire  vmem_io_mem_aw_ready; // @[VCRAccel.scala 99:20]
  wire  vmem_io_mem_aw_valid; // @[VCRAccel.scala 99:20]
  wire [31:0] vmem_io_mem_aw_bits_addr; // @[VCRAccel.scala 99:20]
  wire [7:0] vmem_io_mem_aw_bits_len; // @[VCRAccel.scala 99:20]
  wire  vmem_io_mem_w_ready; // @[VCRAccel.scala 99:20]
  wire  vmem_io_mem_w_valid; // @[VCRAccel.scala 99:20]
  wire [63:0] vmem_io_mem_w_bits_data; // @[VCRAccel.scala 99:20]
  wire  vmem_io_mem_w_bits_last; // @[VCRAccel.scala 99:20]
  wire  vmem_io_mem_b_ready; // @[VCRAccel.scala 99:20]
  wire  vmem_io_mem_b_valid; // @[VCRAccel.scala 99:20]
  wire  vmem_io_mem_ar_ready; // @[VCRAccel.scala 99:20]
  wire  vmem_io_mem_ar_valid; // @[VCRAccel.scala 99:20]
  wire [31:0] vmem_io_mem_ar_bits_addr; // @[VCRAccel.scala 99:20]
  wire [7:0] vmem_io_mem_ar_bits_len; // @[VCRAccel.scala 99:20]
  wire  vmem_io_mem_r_ready; // @[VCRAccel.scala 99:20]
  wire  vmem_io_mem_r_valid; // @[VCRAccel.scala 99:20]
  wire [63:0] vmem_io_mem_r_bits_data; // @[VCRAccel.scala 99:20]
  wire  vmem_io_mem_r_bits_last; // @[VCRAccel.scala 99:20]
  wire  vmem_io_vme_rd_0_cmd_ready; // @[VCRAccel.scala 99:20]
  wire  vmem_io_vme_rd_0_cmd_valid; // @[VCRAccel.scala 99:20]
  wire [31:0] vmem_io_vme_rd_0_cmd_bits_addr; // @[VCRAccel.scala 99:20]
  wire [7:0] vmem_io_vme_rd_0_cmd_bits_len; // @[VCRAccel.scala 99:20]
  wire  vmem_io_vme_rd_0_data_ready; // @[VCRAccel.scala 99:20]
  wire  vmem_io_vme_rd_0_data_valid; // @[VCRAccel.scala 99:20]
  wire [63:0] vmem_io_vme_rd_0_data_bits; // @[VCRAccel.scala 99:20]
  wire  vmem_io_vme_wr_0_cmd_ready; // @[VCRAccel.scala 99:20]
  wire  vmem_io_vme_wr_0_cmd_valid; // @[VCRAccel.scala 99:20]
  wire [31:0] vmem_io_vme_wr_0_cmd_bits_addr; // @[VCRAccel.scala 99:20]
  wire [7:0] vmem_io_vme_wr_0_cmd_bits_len; // @[VCRAccel.scala 99:20]
  wire  vmem_io_vme_wr_0_data_ready; // @[VCRAccel.scala 99:20]
  wire  vmem_io_vme_wr_0_data_valid; // @[VCRAccel.scala 99:20]
  wire [63:0] vmem_io_vme_wr_0_data_bits; // @[VCRAccel.scala 99:20]
  wire  vmem_io_vme_wr_0_ack; // @[VCRAccel.scala 99:20]
  wire  buffer_clock; // @[VCRAccel.scala 100:22]
  wire  buffer_reset; // @[VCRAccel.scala 100:22]
  wire  buffer_io_enq_ready; // @[VCRAccel.scala 100:22]
  wire  buffer_io_enq_valid; // @[VCRAccel.scala 100:22]
  wire [63:0] buffer_io_enq_bits; // @[VCRAccel.scala 100:22]
  wire  buffer_io_deq_ready; // @[VCRAccel.scala 100:22]
  wire  buffer_io_deq_valid; // @[VCRAccel.scala 100:22]
  wire [63:0] buffer_io_deq_bits; // @[VCRAccel.scala 100:22]
  reg [1:0] Rstate; // @[VCRAccel.scala 103:23]
  reg [31:0] _RAND_0;
  reg [1:0] Wstate; // @[VCRAccel.scala 104:23]
  reg [31:0] _RAND_1;
  reg [7:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_2;
  wire  _T; // @[VCRAccel.scala 108:16]
  wire  _T_1; // @[Counter.scala 37:24]
  wire [7:0] _T_3; // @[Counter.scala 38:22]
  wire  _T_4; // @[Conditional.scala 37:30]
  wire  _T_5; // @[Conditional.scala 37:30]
  wire  _T_6; // @[Decoupled.scala 40:37]
  wire  _T_7; // @[Conditional.scala 37:30]
  wire  _T_8; // @[Conditional.scala 37:30]
  wire  _T_9; // @[Decoupled.scala 40:37]
  wire  _T_12; // @[VCRAccel.scala 160:21]
  wire [63:0] _GEN_16; // @[VCRAccel.scala 171:53]
  VCR vcr ( // @[VCRAccel.scala 98:19]
    .clock(vcr_clock),
    .reset(vcr_reset),
    .io_host_aw_ready(vcr_io_host_aw_ready),
    .io_host_aw_valid(vcr_io_host_aw_valid),
    .io_host_aw_bits_addr(vcr_io_host_aw_bits_addr),
    .io_host_w_ready(vcr_io_host_w_ready),
    .io_host_w_valid(vcr_io_host_w_valid),
    .io_host_w_bits_data(vcr_io_host_w_bits_data),
    .io_host_b_ready(vcr_io_host_b_ready),
    .io_host_b_valid(vcr_io_host_b_valid),
    .io_host_ar_ready(vcr_io_host_ar_ready),
    .io_host_ar_valid(vcr_io_host_ar_valid),
    .io_host_ar_bits_addr(vcr_io_host_ar_bits_addr),
    .io_host_r_ready(vcr_io_host_r_ready),
    .io_host_r_valid(vcr_io_host_r_valid),
    .io_host_r_bits_data(vcr_io_host_r_bits_data),
    .io_vcr_launch(vcr_io_vcr_launch),
    .io_vcr_finish(vcr_io_vcr_finish),
    .io_vcr_ecnt_0_valid(vcr_io_vcr_ecnt_0_valid),
    .io_vcr_ecnt_0_bits(vcr_io_vcr_ecnt_0_bits),
    .io_vcr_vals_0(vcr_io_vcr_vals_0),
    .io_vcr_vals_1(vcr_io_vcr_vals_1),
    .io_vcr_ptrs_0(vcr_io_vcr_ptrs_0),
    .io_vcr_ptrs_2(vcr_io_vcr_ptrs_2)
  );
  VME vmem ( // @[VCRAccel.scala 99:20]
    .clock(vmem_clock),
    .reset(vmem_reset),
    .io_mem_aw_ready(vmem_io_mem_aw_ready),
    .io_mem_aw_valid(vmem_io_mem_aw_valid),
    .io_mem_aw_bits_addr(vmem_io_mem_aw_bits_addr),
    .io_mem_aw_bits_len(vmem_io_mem_aw_bits_len),
    .io_mem_w_ready(vmem_io_mem_w_ready),
    .io_mem_w_valid(vmem_io_mem_w_valid),
    .io_mem_w_bits_data(vmem_io_mem_w_bits_data),
    .io_mem_w_bits_last(vmem_io_mem_w_bits_last),
    .io_mem_b_ready(vmem_io_mem_b_ready),
    .io_mem_b_valid(vmem_io_mem_b_valid),
    .io_mem_ar_ready(vmem_io_mem_ar_ready),
    .io_mem_ar_valid(vmem_io_mem_ar_valid),
    .io_mem_ar_bits_addr(vmem_io_mem_ar_bits_addr),
    .io_mem_ar_bits_len(vmem_io_mem_ar_bits_len),
    .io_mem_r_ready(vmem_io_mem_r_ready),
    .io_mem_r_valid(vmem_io_mem_r_valid),
    .io_mem_r_bits_data(vmem_io_mem_r_bits_data),
    .io_mem_r_bits_last(vmem_io_mem_r_bits_last),
    .io_vme_rd_0_cmd_ready(vmem_io_vme_rd_0_cmd_ready),
    .io_vme_rd_0_cmd_valid(vmem_io_vme_rd_0_cmd_valid),
    .io_vme_rd_0_cmd_bits_addr(vmem_io_vme_rd_0_cmd_bits_addr),
    .io_vme_rd_0_cmd_bits_len(vmem_io_vme_rd_0_cmd_bits_len),
    .io_vme_rd_0_data_ready(vmem_io_vme_rd_0_data_ready),
    .io_vme_rd_0_data_valid(vmem_io_vme_rd_0_data_valid),
    .io_vme_rd_0_data_bits(vmem_io_vme_rd_0_data_bits),
    .io_vme_wr_0_cmd_ready(vmem_io_vme_wr_0_cmd_ready),
    .io_vme_wr_0_cmd_valid(vmem_io_vme_wr_0_cmd_valid),
    .io_vme_wr_0_cmd_bits_addr(vmem_io_vme_wr_0_cmd_bits_addr),
    .io_vme_wr_0_cmd_bits_len(vmem_io_vme_wr_0_cmd_bits_len),
    .io_vme_wr_0_data_ready(vmem_io_vme_wr_0_data_ready),
    .io_vme_wr_0_data_valid(vmem_io_vme_wr_0_data_valid),
    .io_vme_wr_0_data_bits(vmem_io_vme_wr_0_data_bits),
    .io_vme_wr_0_ack(vmem_io_vme_wr_0_ack)
  );
  Queue buffer ( // @[VCRAccel.scala 100:22]
    .clock(buffer_clock),
    .reset(buffer_reset),
    .io_enq_ready(buffer_io_enq_ready),
    .io_enq_valid(buffer_io_enq_valid),
    .io_enq_bits(buffer_io_enq_bits),
    .io_deq_ready(buffer_io_deq_ready),
    .io_deq_valid(buffer_io_deq_valid),
    .io_deq_bits(buffer_io_deq_bits)
  );
  assign _T = Rstate != 2'h0; // @[VCRAccel.scala 108:16]
  assign _T_1 = value == 8'hc7; // @[Counter.scala 37:24]
  assign _T_3 = value + 8'h1; // @[Counter.scala 38:22]
  assign _T_4 = 2'h0 == Rstate; // @[Conditional.scala 37:30]
  assign _T_5 = 2'h1 == Rstate; // @[Conditional.scala 37:30]
  assign _T_6 = vmem_io_vme_rd_0_cmd_ready & vmem_io_vme_rd_0_cmd_valid; // @[Decoupled.scala 40:37]
  assign _T_7 = 2'h0 == Wstate; // @[Conditional.scala 37:30]
  assign _T_8 = 2'h1 == Wstate; // @[Conditional.scala 37:30]
  assign _T_9 = vmem_io_vme_wr_0_cmd_ready & vmem_io_vme_wr_0_cmd_valid; // @[Decoupled.scala 40:37]
  assign _T_12 = Wstate == 2'h2; // @[VCRAccel.scala 160:21]
  assign _GEN_16 = {{32'd0}, vcr_io_vcr_vals_0}; // @[VCRAccel.scala 171:53]
  assign io_host_aw_ready = vcr_io_host_aw_ready; // @[VCRAccel.scala 175:11]
  assign io_host_w_ready = vcr_io_host_w_ready; // @[VCRAccel.scala 175:11]
  assign io_host_b_valid = vcr_io_host_b_valid; // @[VCRAccel.scala 175:11]
  assign io_host_ar_ready = vcr_io_host_ar_ready; // @[VCRAccel.scala 175:11]
  assign io_host_r_valid = vcr_io_host_r_valid; // @[VCRAccel.scala 175:11]
  assign io_host_r_bits_data = vcr_io_host_r_bits_data; // @[VCRAccel.scala 175:11]
  assign io_mem_aw_valid = vmem_io_mem_aw_valid; // @[VCRAccel.scala 174:10]
  assign io_mem_aw_bits_addr = vmem_io_mem_aw_bits_addr; // @[VCRAccel.scala 174:10]
  assign io_mem_aw_bits_len = vmem_io_mem_aw_bits_len; // @[VCRAccel.scala 174:10]
  assign io_mem_w_valid = vmem_io_mem_w_valid; // @[VCRAccel.scala 174:10]
  assign io_mem_w_bits_data = vmem_io_mem_w_bits_data; // @[VCRAccel.scala 174:10]
  assign io_mem_w_bits_last = vmem_io_mem_w_bits_last; // @[VCRAccel.scala 174:10]
  assign io_mem_b_ready = vmem_io_mem_b_ready; // @[VCRAccel.scala 174:10]
  assign io_mem_ar_valid = vmem_io_mem_ar_valid; // @[VCRAccel.scala 174:10]
  assign io_mem_ar_bits_addr = vmem_io_mem_ar_bits_addr; // @[VCRAccel.scala 174:10]
  assign io_mem_ar_bits_len = vmem_io_mem_ar_bits_len; // @[VCRAccel.scala 174:10]
  assign io_mem_r_ready = vmem_io_mem_r_ready; // @[VCRAccel.scala 174:10]
  assign vcr_clock = clock;
  assign vcr_reset = reset;
  assign vcr_io_host_aw_valid = io_host_aw_valid; // @[VCRAccel.scala 175:11]
  assign vcr_io_host_aw_bits_addr = io_host_aw_bits_addr; // @[VCRAccel.scala 175:11]
  assign vcr_io_host_w_valid = io_host_w_valid; // @[VCRAccel.scala 175:11]
  assign vcr_io_host_w_bits_data = io_host_w_bits_data; // @[VCRAccel.scala 175:11]
  assign vcr_io_host_b_ready = io_host_b_ready; // @[VCRAccel.scala 175:11]
  assign vcr_io_host_ar_valid = io_host_ar_valid; // @[VCRAccel.scala 175:11]
  assign vcr_io_host_ar_bits_addr = io_host_ar_bits_addr; // @[VCRAccel.scala 175:11]
  assign vcr_io_host_r_ready = io_host_r_ready; // @[VCRAccel.scala 175:11]
  assign vcr_io_vcr_finish = _T_12 & vmem_io_vme_wr_0_ack; // @[VCRAccel.scala 161:21]
  assign vcr_io_vcr_ecnt_0_valid = _T_12 & vmem_io_vme_wr_0_ack; // @[VCRAccel.scala 162:28]
  assign vcr_io_vcr_ecnt_0_bits = {{24'd0}, value}; // @[VCRAccel.scala 113:29]
  assign vmem_clock = clock;
  assign vmem_reset = reset;
  assign vmem_io_mem_aw_ready = io_mem_aw_ready; // @[VCRAccel.scala 174:10]
  assign vmem_io_mem_w_ready = io_mem_w_ready; // @[VCRAccel.scala 174:10]
  assign vmem_io_mem_b_valid = io_mem_b_valid; // @[VCRAccel.scala 174:10]
  assign vmem_io_mem_ar_ready = io_mem_ar_ready; // @[VCRAccel.scala 174:10]
  assign vmem_io_mem_r_valid = io_mem_r_valid; // @[VCRAccel.scala 174:10]
  assign vmem_io_mem_r_bits_data = io_mem_r_bits_data; // @[VCRAccel.scala 174:10]
  assign vmem_io_mem_r_bits_last = io_mem_r_bits_last; // @[VCRAccel.scala 174:10]
  assign vmem_io_vme_rd_0_cmd_valid = Rstate == 2'h1; // @[VCRAccel.scala 145:31 VCRAccel.scala 152:33]
  assign vmem_io_vme_rd_0_cmd_bits_addr = vcr_io_vcr_ptrs_0; // @[VCRAccel.scala 143:35]
  assign vmem_io_vme_rd_0_cmd_bits_len = vcr_io_vcr_vals_1[7:0]; // @[VCRAccel.scala 144:34]
  assign vmem_io_vme_rd_0_data_ready = buffer_io_enq_ready; // @[VCRAccel.scala 170:17]
  assign vmem_io_vme_wr_0_cmd_valid = Wstate == 2'h1; // @[VCRAccel.scala 149:31 VCRAccel.scala 156:33]
  assign vmem_io_vme_wr_0_cmd_bits_addr = vcr_io_vcr_ptrs_2; // @[VCRAccel.scala 147:35]
  assign vmem_io_vme_wr_0_cmd_bits_len = vcr_io_vcr_vals_1[7:0]; // @[VCRAccel.scala 148:34]
  assign vmem_io_vme_wr_0_data_valid = buffer_io_deq_valid; // @[VCRAccel.scala 172:26]
  assign vmem_io_vme_wr_0_data_bits = buffer_io_deq_bits; // @[VCRAccel.scala 172:26]
  assign buffer_clock = clock;
  assign buffer_reset = reset;
  assign buffer_io_enq_valid = vmem_io_vme_rd_0_data_valid; // @[VCRAccel.scala 170:17]
  assign buffer_io_enq_bits = vmem_io_vme_rd_0_data_bits + _GEN_16; // @[VCRAccel.scala 170:17 VCRAccel.scala 171:22]
  assign buffer_io_deq_ready = vmem_io_vme_wr_0_data_ready; // @[VCRAccel.scala 172:26]
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  Rstate = _RAND_0[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  Wstate = _RAND_1[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  value = _RAND_2[7:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end
  always @(posedge clock) begin
    if (reset) begin
      Rstate <= 2'h0;
    end else begin
      if (vmem_io_vme_wr_0_ack) begin
        Rstate <= 2'h0;
      end else begin
        if (_T_4) begin
          if (vcr_io_vcr_launch) begin
            Rstate <= 2'h1;
          end
        end else begin
          if (_T_5) begin
            if (_T_6) begin
              Rstate <= 2'h2;
            end
          end
        end
      end
    end
    if (reset) begin
      Wstate <= 2'h0;
    end else begin
      if (vmem_io_vme_wr_0_ack) begin
        Wstate <= 2'h0;
      end else begin
        if (_T_7) begin
          if (vcr_io_vcr_launch) begin
            Wstate <= 2'h1;
          end
        end else begin
          if (_T_8) begin
            if (_T_9) begin
              Wstate <= 2'h2;
            end
          end
        end
      end
    end
    if (reset) begin
      value <= 8'h0;
    end else begin
      if (_T_4) begin
        if (vcr_io_vcr_launch) begin
          value <= 8'h0;
        end else begin
          if (_T) begin
            if (_T_1) begin
              value <= 8'h0;
            end else begin
              value <= _T_3;
            end
          end
        end
      end else begin
        if (_T) begin
          if (_T_1) begin
            value <= 8'h0;
          end else begin
            value <= _T_3;
          end
        end
      end
    end
  end
endmodule
module TestAccel2(
  input   clock,
  input   reset,
  input   sim_clock,
  output  sim_wait
);
  wire  sim_shell_clock; // @[VCRAccel.scala 186:25]
  wire  sim_shell_reset; // @[VCRAccel.scala 186:25]
  wire  sim_shell_mem_aw_ready; // @[VCRAccel.scala 186:25]
  wire  sim_shell_mem_aw_valid; // @[VCRAccel.scala 186:25]
  wire [31:0] sim_shell_mem_aw_bits_addr; // @[VCRAccel.scala 186:25]
  wire [7:0] sim_shell_mem_aw_bits_len; // @[VCRAccel.scala 186:25]
  wire  sim_shell_mem_w_ready; // @[VCRAccel.scala 186:25]
  wire  sim_shell_mem_w_valid; // @[VCRAccel.scala 186:25]
  wire [63:0] sim_shell_mem_w_bits_data; // @[VCRAccel.scala 186:25]
  wire  sim_shell_mem_w_bits_last; // @[VCRAccel.scala 186:25]
  wire  sim_shell_mem_b_ready; // @[VCRAccel.scala 186:25]
  wire  sim_shell_mem_b_valid; // @[VCRAccel.scala 186:25]
  wire  sim_shell_mem_ar_ready; // @[VCRAccel.scala 186:25]
  wire  sim_shell_mem_ar_valid; // @[VCRAccel.scala 186:25]
  wire [31:0] sim_shell_mem_ar_bits_addr; // @[VCRAccel.scala 186:25]
  wire [7:0] sim_shell_mem_ar_bits_len; // @[VCRAccel.scala 186:25]
  wire  sim_shell_mem_r_ready; // @[VCRAccel.scala 186:25]
  wire  sim_shell_mem_r_valid; // @[VCRAccel.scala 186:25]
  wire [63:0] sim_shell_mem_r_bits_data; // @[VCRAccel.scala 186:25]
  wire  sim_shell_mem_r_bits_last; // @[VCRAccel.scala 186:25]
  wire  sim_shell_host_aw_ready; // @[VCRAccel.scala 186:25]
  wire  sim_shell_host_aw_valid; // @[VCRAccel.scala 186:25]
  wire [31:0] sim_shell_host_aw_bits_addr; // @[VCRAccel.scala 186:25]
  wire  sim_shell_host_w_ready; // @[VCRAccel.scala 186:25]
  wire  sim_shell_host_w_valid; // @[VCRAccel.scala 186:25]
  wire [31:0] sim_shell_host_w_bits_data; // @[VCRAccel.scala 186:25]
  wire  sim_shell_host_b_ready; // @[VCRAccel.scala 186:25]
  wire  sim_shell_host_b_valid; // @[VCRAccel.scala 186:25]
  wire  sim_shell_host_ar_ready; // @[VCRAccel.scala 186:25]
  wire  sim_shell_host_ar_valid; // @[VCRAccel.scala 186:25]
  wire [31:0] sim_shell_host_ar_bits_addr; // @[VCRAccel.scala 186:25]
  wire  sim_shell_host_r_ready; // @[VCRAccel.scala 186:25]
  wire  sim_shell_host_r_valid; // @[VCRAccel.scala 186:25]
  wire [31:0] sim_shell_host_r_bits_data; // @[VCRAccel.scala 186:25]
  wire  sim_shell_sim_clock; // @[VCRAccel.scala 186:25]
  wire  sim_shell_sim_wait; // @[VCRAccel.scala 186:25]
  wire  vta_shell_clock; // @[VCRAccel.scala 187:25]
  wire  vta_shell_reset; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_host_aw_ready; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_host_aw_valid; // @[VCRAccel.scala 187:25]
  wire [31:0] vta_shell_io_host_aw_bits_addr; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_host_w_ready; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_host_w_valid; // @[VCRAccel.scala 187:25]
  wire [31:0] vta_shell_io_host_w_bits_data; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_host_b_ready; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_host_b_valid; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_host_ar_ready; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_host_ar_valid; // @[VCRAccel.scala 187:25]
  wire [31:0] vta_shell_io_host_ar_bits_addr; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_host_r_ready; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_host_r_valid; // @[VCRAccel.scala 187:25]
  wire [31:0] vta_shell_io_host_r_bits_data; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_mem_aw_ready; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_mem_aw_valid; // @[VCRAccel.scala 187:25]
  wire [31:0] vta_shell_io_mem_aw_bits_addr; // @[VCRAccel.scala 187:25]
  wire [7:0] vta_shell_io_mem_aw_bits_len; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_mem_w_ready; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_mem_w_valid; // @[VCRAccel.scala 187:25]
  wire [63:0] vta_shell_io_mem_w_bits_data; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_mem_w_bits_last; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_mem_b_ready; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_mem_b_valid; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_mem_ar_ready; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_mem_ar_valid; // @[VCRAccel.scala 187:25]
  wire [31:0] vta_shell_io_mem_ar_bits_addr; // @[VCRAccel.scala 187:25]
  wire [7:0] vta_shell_io_mem_ar_bits_len; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_mem_r_ready; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_mem_r_valid; // @[VCRAccel.scala 187:25]
  wire [63:0] vta_shell_io_mem_r_bits_data; // @[VCRAccel.scala 187:25]
  wire  vta_shell_io_mem_r_bits_last; // @[VCRAccel.scala 187:25]
  AXISimShell sim_shell ( // @[VCRAccel.scala 186:25]
    .clock(sim_shell_clock),
    .reset(sim_shell_reset),
    .mem_aw_ready(sim_shell_mem_aw_ready),
    .mem_aw_valid(sim_shell_mem_aw_valid),
    .mem_aw_bits_addr(sim_shell_mem_aw_bits_addr),
    .mem_aw_bits_len(sim_shell_mem_aw_bits_len),
    .mem_w_ready(sim_shell_mem_w_ready),
    .mem_w_valid(sim_shell_mem_w_valid),
    .mem_w_bits_data(sim_shell_mem_w_bits_data),
    .mem_w_bits_last(sim_shell_mem_w_bits_last),
    .mem_b_ready(sim_shell_mem_b_ready),
    .mem_b_valid(sim_shell_mem_b_valid),
    .mem_ar_ready(sim_shell_mem_ar_ready),
    .mem_ar_valid(sim_shell_mem_ar_valid),
    .mem_ar_bits_addr(sim_shell_mem_ar_bits_addr),
    .mem_ar_bits_len(sim_shell_mem_ar_bits_len),
    .mem_r_ready(sim_shell_mem_r_ready),
    .mem_r_valid(sim_shell_mem_r_valid),
    .mem_r_bits_data(sim_shell_mem_r_bits_data),
    .mem_r_bits_last(sim_shell_mem_r_bits_last),
    .host_aw_ready(sim_shell_host_aw_ready),
    .host_aw_valid(sim_shell_host_aw_valid),
    .host_aw_bits_addr(sim_shell_host_aw_bits_addr),
    .host_w_ready(sim_shell_host_w_ready),
    .host_w_valid(sim_shell_host_w_valid),
    .host_w_bits_data(sim_shell_host_w_bits_data),
    .host_b_ready(sim_shell_host_b_ready),
    .host_b_valid(sim_shell_host_b_valid),
    .host_ar_ready(sim_shell_host_ar_ready),
    .host_ar_valid(sim_shell_host_ar_valid),
    .host_ar_bits_addr(sim_shell_host_ar_bits_addr),
    .host_r_ready(sim_shell_host_r_ready),
    .host_r_valid(sim_shell_host_r_valid),
    .host_r_bits_data(sim_shell_host_r_bits_data),
    .sim_clock(sim_shell_sim_clock),
    .sim_wait(sim_shell_sim_wait)
  );
  VTAShell2 vta_shell ( // @[VCRAccel.scala 187:25]
    .clock(vta_shell_clock),
    .reset(vta_shell_reset),
    .io_host_aw_ready(vta_shell_io_host_aw_ready),
    .io_host_aw_valid(vta_shell_io_host_aw_valid),
    .io_host_aw_bits_addr(vta_shell_io_host_aw_bits_addr),
    .io_host_w_ready(vta_shell_io_host_w_ready),
    .io_host_w_valid(vta_shell_io_host_w_valid),
    .io_host_w_bits_data(vta_shell_io_host_w_bits_data),
    .io_host_b_ready(vta_shell_io_host_b_ready),
    .io_host_b_valid(vta_shell_io_host_b_valid),
    .io_host_ar_ready(vta_shell_io_host_ar_ready),
    .io_host_ar_valid(vta_shell_io_host_ar_valid),
    .io_host_ar_bits_addr(vta_shell_io_host_ar_bits_addr),
    .io_host_r_ready(vta_shell_io_host_r_ready),
    .io_host_r_valid(vta_shell_io_host_r_valid),
    .io_host_r_bits_data(vta_shell_io_host_r_bits_data),
    .io_mem_aw_ready(vta_shell_io_mem_aw_ready),
    .io_mem_aw_valid(vta_shell_io_mem_aw_valid),
    .io_mem_aw_bits_addr(vta_shell_io_mem_aw_bits_addr),
    .io_mem_aw_bits_len(vta_shell_io_mem_aw_bits_len),
    .io_mem_w_ready(vta_shell_io_mem_w_ready),
    .io_mem_w_valid(vta_shell_io_mem_w_valid),
    .io_mem_w_bits_data(vta_shell_io_mem_w_bits_data),
    .io_mem_w_bits_last(vta_shell_io_mem_w_bits_last),
    .io_mem_b_ready(vta_shell_io_mem_b_ready),
    .io_mem_b_valid(vta_shell_io_mem_b_valid),
    .io_mem_ar_ready(vta_shell_io_mem_ar_ready),
    .io_mem_ar_valid(vta_shell_io_mem_ar_valid),
    .io_mem_ar_bits_addr(vta_shell_io_mem_ar_bits_addr),
    .io_mem_ar_bits_len(vta_shell_io_mem_ar_bits_len),
    .io_mem_r_ready(vta_shell_io_mem_r_ready),
    .io_mem_r_valid(vta_shell_io_mem_r_valid),
    .io_mem_r_bits_data(vta_shell_io_mem_r_bits_data),
    .io_mem_r_bits_last(vta_shell_io_mem_r_bits_last)
  );
  assign sim_wait = sim_shell_sim_wait; // @[VCRAccel.scala 189:12]
  assign sim_shell_clock = clock;
  assign sim_shell_reset = reset;
  assign sim_shell_mem_aw_valid = vta_shell_io_mem_aw_valid; // @[VCRAccel.scala 192:20]
  assign sim_shell_mem_aw_bits_addr = vta_shell_io_mem_aw_bits_addr; // @[VCRAccel.scala 192:20]
  assign sim_shell_mem_aw_bits_len = vta_shell_io_mem_aw_bits_len; // @[VCRAccel.scala 192:20]
  assign sim_shell_mem_w_valid = vta_shell_io_mem_w_valid; // @[VCRAccel.scala 195:19]
  assign sim_shell_mem_w_bits_data = vta_shell_io_mem_w_bits_data; // @[VCRAccel.scala 195:19]
  assign sim_shell_mem_w_bits_last = vta_shell_io_mem_w_bits_last; // @[VCRAccel.scala 195:19]
  assign sim_shell_mem_b_ready = vta_shell_io_mem_b_ready; // @[VCRAccel.scala 194:22]
  assign sim_shell_mem_ar_valid = vta_shell_io_mem_ar_valid; // @[VCRAccel.scala 191:20]
  assign sim_shell_mem_ar_bits_addr = vta_shell_io_mem_ar_bits_addr; // @[VCRAccel.scala 191:20]
  assign sim_shell_mem_ar_bits_len = vta_shell_io_mem_ar_bits_len; // @[VCRAccel.scala 191:20]
  assign sim_shell_mem_r_ready = vta_shell_io_mem_r_ready; // @[VCRAccel.scala 193:22]
  assign sim_shell_host_aw_ready = vta_shell_io_host_aw_ready; // @[VCRAccel.scala 200:24]
  assign sim_shell_host_w_ready = vta_shell_io_host_w_ready; // @[VCRAccel.scala 203:23]
  assign sim_shell_host_b_valid = vta_shell_io_host_b_valid; // @[VCRAccel.scala 202:20]
  assign sim_shell_host_ar_ready = vta_shell_io_host_ar_ready; // @[VCRAccel.scala 199:24]
  assign sim_shell_host_r_valid = vta_shell_io_host_r_valid; // @[VCRAccel.scala 201:20]
  assign sim_shell_host_r_bits_data = vta_shell_io_host_r_bits_data; // @[VCRAccel.scala 201:20]
  assign sim_shell_sim_clock = sim_clock; // @[VCRAccel.scala 188:23]
  assign vta_shell_clock = clock;
  assign vta_shell_reset = reset;
  assign vta_shell_io_host_aw_valid = sim_shell_host_aw_valid; // @[VCRAccel.scala 200:24]
  assign vta_shell_io_host_aw_bits_addr = sim_shell_host_aw_bits_addr; // @[VCRAccel.scala 200:24]
  assign vta_shell_io_host_w_valid = sim_shell_host_w_valid; // @[VCRAccel.scala 203:23]
  assign vta_shell_io_host_w_bits_data = sim_shell_host_w_bits_data; // @[VCRAccel.scala 203:23]
  assign vta_shell_io_host_b_ready = sim_shell_host_b_ready; // @[VCRAccel.scala 202:20]
  assign vta_shell_io_host_ar_valid = sim_shell_host_ar_valid; // @[VCRAccel.scala 199:24]
  assign vta_shell_io_host_ar_bits_addr = sim_shell_host_ar_bits_addr; // @[VCRAccel.scala 199:24]
  assign vta_shell_io_host_r_ready = sim_shell_host_r_ready; // @[VCRAccel.scala 201:20]
  assign vta_shell_io_mem_aw_ready = sim_shell_mem_aw_ready; // @[VCRAccel.scala 192:20]
  assign vta_shell_io_mem_w_ready = sim_shell_mem_w_ready; // @[VCRAccel.scala 195:19]
  assign vta_shell_io_mem_b_valid = sim_shell_mem_b_valid; // @[VCRAccel.scala 194:22]
  assign vta_shell_io_mem_ar_ready = sim_shell_mem_ar_ready; // @[VCRAccel.scala 191:20]
  assign vta_shell_io_mem_r_valid = sim_shell_mem_r_valid; // @[VCRAccel.scala 193:22]
  assign vta_shell_io_mem_r_bits_data = sim_shell_mem_r_bits_data; // @[VCRAccel.scala 193:22]
  assign vta_shell_io_mem_r_bits_last = sim_shell_mem_r_bits_last; // @[VCRAccel.scala 193:22]
endmodule
