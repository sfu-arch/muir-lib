module VTASim( // @[:@11.2]
  input   clock, // @[:@12.4]
  input   reset, // @[:@13.4]
  output  sim_wait // @[:@14.4]
);
  wire  sim_dpi_wait; // @[SimShell.scala 74:19:@16.4]
  wire  sim_reset; // @[SimShell.scala 74:19:@16.4]
  wire  sim_clock; // @[SimShell.scala 74:19:@16.4]
  VTASimDPI sim ( // @[SimShell.scala 74:19:@16.4]
    .dpi_wait(sim_dpi_wait),
    .reset(sim_reset),
    .clock(sim_clock)
  );
  assign sim_wait = sim_dpi_wait; // @[SimShell.scala 77:12:@22.4]
  assign sim_reset = reset; // @[SimShell.scala 75:16:@20.4]
  assign sim_clock = clock; // @[SimShell.scala 76:16:@21.4]
endmodule
module VTAHostDPIToAXI( // @[:@32.2]
  input         clock, // @[:@33.4]
  input         reset, // @[:@34.4]
  input         io_dpi_req_valid, // @[:@35.4]
  input         io_dpi_req_opcode, // @[:@35.4]
  input  [7:0]  io_dpi_req_addr, // @[:@35.4]
  input  [31:0] io_dpi_req_value, // @[:@35.4]
  output        io_dpi_req_deq, // @[:@35.4]
  output        io_dpi_resp_valid, // @[:@35.4]
  output [31:0] io_dpi_resp_bits, // @[:@35.4]
  input         io_axi_aw_ready, // @[:@35.4]
  output        io_axi_aw_valid, // @[:@35.4]
  output [15:0] io_axi_aw_bits_addr, // @[:@35.4]
  input         io_axi_w_ready, // @[:@35.4]
  output        io_axi_w_valid, // @[:@35.4]
  output [31:0] io_axi_w_bits_data, // @[:@35.4]
  output        io_axi_b_ready, // @[:@35.4]
  input         io_axi_b_valid, // @[:@35.4]
  input         io_axi_ar_ready, // @[:@35.4]
  output        io_axi_ar_valid, // @[:@35.4]
  output [15:0] io_axi_ar_bits_addr, // @[:@35.4]
  output        io_axi_r_ready, // @[:@35.4]
  input         io_axi_r_valid, // @[:@35.4]
  input  [31:0] io_axi_r_bits_data // @[:@35.4]
);
  reg [7:0] addr; // @[VTAHostDPI.scala 87:21:@39.4]
  reg [31:0] _RAND_0;
  reg [31:0] data; // @[VTAHostDPI.scala 88:21:@42.4]
  reg [31:0] _RAND_1;
  reg [2:0] state; // @[VTAHostDPI.scala 90:22:@43.4]
  reg [31:0] _RAND_2;
  wire  _T_72; // @[Conditional.scala 37:30:@44.4]
  wire [2:0] _GEN_0; // @[VTAHostDPI.scala 95:34:@47.8]
  wire [2:0] _GEN_1; // @[VTAHostDPI.scala 94:31:@46.6]
  wire  _T_73; // @[Conditional.scala 37:30:@56.6]
  wire [2:0] _GEN_2; // @[VTAHostDPI.scala 103:30:@58.8]
  wire  _T_74; // @[Conditional.scala 37:30:@63.8]
  wire [2:0] _GEN_3; // @[VTAHostDPI.scala 108:29:@65.10]
  wire  _T_75; // @[Conditional.scala 37:30:@70.10]
  wire [2:0] _GEN_4; // @[VTAHostDPI.scala 113:30:@72.12]
  wire  _T_76; // @[Conditional.scala 37:30:@77.12]
  wire [2:0] _GEN_5; // @[VTAHostDPI.scala 118:29:@79.14]
  wire  _T_77; // @[Conditional.scala 37:30:@84.14]
  wire [2:0] _GEN_6; // @[VTAHostDPI.scala 123:29:@86.16]
  wire [2:0] _GEN_7; // @[Conditional.scala 39:67:@85.14]
  wire [2:0] _GEN_8; // @[Conditional.scala 39:67:@78.12]
  wire [2:0] _GEN_9; // @[Conditional.scala 39:67:@71.10]
  wire [2:0] _GEN_10; // @[Conditional.scala 39:67:@64.8]
  wire [2:0] _GEN_11; // @[Conditional.scala 39:67:@57.6]
  wire [2:0] _GEN_12; // @[Conditional.scala 40:58:@45.4]
  wire  _T_78; // @[VTAHostDPI.scala 129:15:@90.4]
  wire  _T_79; // @[VTAHostDPI.scala 129:25:@91.4]
  wire [7:0] _GEN_13; // @[VTAHostDPI.scala 129:46:@92.4]
  wire [31:0] _GEN_14; // @[VTAHostDPI.scala 129:46:@92.4]
  wire  _T_80; // @[VTAHostDPI.scala 134:28:@96.4]
  wire  _T_84; // @[VTAHostDPI.scala 141:28:@105.4]
  wire  _T_87; // @[VTAHostDPI.scala 145:45:@111.4]
  wire  _T_89; // @[VTAHostDPI.scala 145:91:@113.4]
  assign _T_72 = 3'h0 == state; // @[Conditional.scala 37:30:@44.4]
  assign _GEN_0 = io_dpi_req_opcode ? 3'h3 : 3'h1; // @[VTAHostDPI.scala 95:34:@47.8]
  assign _GEN_1 = io_dpi_req_valid ? _GEN_0 : state; // @[VTAHostDPI.scala 94:31:@46.6]
  assign _T_73 = 3'h1 == state; // @[Conditional.scala 37:30:@56.6]
  assign _GEN_2 = io_axi_ar_ready ? 3'h2 : state; // @[VTAHostDPI.scala 103:30:@58.8]
  assign _T_74 = 3'h2 == state; // @[Conditional.scala 37:30:@63.8]
  assign _GEN_3 = io_axi_r_valid ? 3'h0 : state; // @[VTAHostDPI.scala 108:29:@65.10]
  assign _T_75 = 3'h3 == state; // @[Conditional.scala 37:30:@70.10]
  assign _GEN_4 = io_axi_aw_ready ? 3'h4 : state; // @[VTAHostDPI.scala 113:30:@72.12]
  assign _T_76 = 3'h4 == state; // @[Conditional.scala 37:30:@77.12]
  assign _GEN_5 = io_axi_w_ready ? 3'h5 : state; // @[VTAHostDPI.scala 118:29:@79.14]
  assign _T_77 = 3'h5 == state; // @[Conditional.scala 37:30:@84.14]
  assign _GEN_6 = io_axi_b_valid ? 3'h0 : state; // @[VTAHostDPI.scala 123:29:@86.16]
  assign _GEN_7 = _T_77 ? _GEN_6 : state; // @[Conditional.scala 39:67:@85.14]
  assign _GEN_8 = _T_76 ? _GEN_5 : _GEN_7; // @[Conditional.scala 39:67:@78.12]
  assign _GEN_9 = _T_75 ? _GEN_4 : _GEN_8; // @[Conditional.scala 39:67:@71.10]
  assign _GEN_10 = _T_74 ? _GEN_3 : _GEN_9; // @[Conditional.scala 39:67:@64.8]
  assign _GEN_11 = _T_73 ? _GEN_2 : _GEN_10; // @[Conditional.scala 39:67:@57.6]
  assign _GEN_12 = _T_72 ? _GEN_1 : _GEN_11; // @[Conditional.scala 40:58:@45.4]
  assign _T_78 = state == 3'h0; // @[VTAHostDPI.scala 129:15:@90.4]
  assign _T_79 = _T_78 & io_dpi_req_valid; // @[VTAHostDPI.scala 129:25:@91.4]
  assign _GEN_13 = _T_79 ? io_dpi_req_addr : addr; // @[VTAHostDPI.scala 129:46:@92.4]
  assign _GEN_14 = _T_79 ? io_dpi_req_value : data; // @[VTAHostDPI.scala 129:46:@92.4]
  assign _T_80 = state == 3'h3; // @[VTAHostDPI.scala 134:28:@96.4]
  assign _T_84 = state == 3'h1; // @[VTAHostDPI.scala 141:28:@105.4]
  assign _T_87 = _T_84 & io_axi_ar_ready; // @[VTAHostDPI.scala 145:45:@111.4]
  assign _T_89 = _T_80 & io_axi_aw_ready; // @[VTAHostDPI.scala 145:91:@113.4]
  assign io_dpi_req_deq = _T_87 | _T_89; // @[VTAHostDPI.scala 145:18:@115.4]
  assign io_dpi_resp_valid = io_axi_r_valid; // @[VTAHostDPI.scala 146:21:@116.4]
  assign io_dpi_resp_bits = io_axi_r_bits_data; // @[VTAHostDPI.scala 147:20:@117.4]
  assign io_axi_aw_valid = state == 3'h3; // @[VTAHostDPI.scala 134:19:@97.4]
  assign io_axi_aw_bits_addr = {{8'd0}, addr}; // @[VTAHostDPI.scala 135:23:@98.4]
  assign io_axi_w_valid = state == 3'h4; // @[VTAHostDPI.scala 136:18:@100.4]
  assign io_axi_w_bits_data = data; // @[VTAHostDPI.scala 137:22:@101.4]
  assign io_axi_b_ready = state == 3'h5; // @[VTAHostDPI.scala 139:18:@104.4]
  assign io_axi_ar_valid = state == 3'h1; // @[VTAHostDPI.scala 141:19:@106.4]
  assign io_axi_ar_bits_addr = {{8'd0}, addr}; // @[VTAHostDPI.scala 142:23:@107.4]
  assign io_axi_r_ready = state == 3'h2; // @[VTAHostDPI.scala 143:18:@109.4]
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
`ifdef RANDOMIZE
  integer initvar;
  initial begin
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      #0.002 begin end
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
  end
`endif // RANDOMIZE
  always @(posedge clock) begin
    if (reset) begin
      addr <= 8'h0;
    end else begin
      if (_T_79) begin
        addr <= io_dpi_req_addr;
      end
    end
    if (reset) begin
      data <= 32'h0;
    end else begin
      if (_T_79) begin
        data <= io_dpi_req_value;
      end
    end
    if (reset) begin
      state <= 3'h0;
    end else begin
      if (_T_72) begin
        if (io_dpi_req_valid) begin
          if (io_dpi_req_opcode) begin
            state <= 3'h3;
          end else begin
            state <= 3'h1;
          end
        end
      end else begin
        if (_T_73) begin
          if (io_axi_ar_ready) begin
            state <= 3'h2;
          end
        end else begin
          if (_T_74) begin
            if (io_axi_r_valid) begin
              state <= 3'h0;
            end
          end else begin
            if (_T_75) begin
              if (io_axi_aw_ready) begin
                state <= 3'h4;
              end
            end else begin
              if (_T_76) begin
                if (io_axi_w_ready) begin
                  state <= 3'h5;
                end
              end else begin
                if (_T_77) begin
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
module VTAHost( // @[:@119.2]
  input         clock, // @[:@120.4]
  input         reset, // @[:@121.4]
  input         io_axi_aw_ready, // @[:@122.4]
  output        io_axi_aw_valid, // @[:@122.4]
  output [15:0] io_axi_aw_bits_addr, // @[:@122.4]
  input         io_axi_w_ready, // @[:@122.4]
  output        io_axi_w_valid, // @[:@122.4]
  output [31:0] io_axi_w_bits_data, // @[:@122.4]
  output        io_axi_b_ready, // @[:@122.4]
  input         io_axi_b_valid, // @[:@122.4]
  input         io_axi_ar_ready, // @[:@122.4]
  output        io_axi_ar_valid, // @[:@122.4]
  output [15:0] io_axi_ar_bits_addr, // @[:@122.4]
  output        io_axi_r_ready, // @[:@122.4]
  input         io_axi_r_valid, // @[:@122.4]
  input  [31:0] io_axi_r_bits_data // @[:@122.4]
);
  wire  host_dpi_dpi_req_valid; // @[SimShell.scala 39:24:@124.4]
  wire  host_dpi_dpi_req_opcode; // @[SimShell.scala 39:24:@124.4]
  wire [7:0] host_dpi_dpi_req_addr; // @[SimShell.scala 39:24:@124.4]
  wire [31:0] host_dpi_dpi_req_value; // @[SimShell.scala 39:24:@124.4]
  wire  host_dpi_dpi_req_deq; // @[SimShell.scala 39:24:@124.4]
  wire  host_dpi_dpi_resp_valid; // @[SimShell.scala 39:24:@124.4]
  wire [31:0] host_dpi_dpi_resp_bits; // @[SimShell.scala 39:24:@124.4]
  wire  host_dpi_reset; // @[SimShell.scala 39:24:@124.4]
  wire  host_dpi_clock; // @[SimShell.scala 39:24:@124.4]
  wire  host_axi_clock; // @[SimShell.scala 40:24:@128.4]
  wire  host_axi_reset; // @[SimShell.scala 40:24:@128.4]
  wire  host_axi_io_dpi_req_valid; // @[SimShell.scala 40:24:@128.4]
  wire  host_axi_io_dpi_req_opcode; // @[SimShell.scala 40:24:@128.4]
  wire [7:0] host_axi_io_dpi_req_addr; // @[SimShell.scala 40:24:@128.4]
  wire [31:0] host_axi_io_dpi_req_value; // @[SimShell.scala 40:24:@128.4]
  wire  host_axi_io_dpi_req_deq; // @[SimShell.scala 40:24:@128.4]
  wire  host_axi_io_dpi_resp_valid; // @[SimShell.scala 40:24:@128.4]
  wire [31:0] host_axi_io_dpi_resp_bits; // @[SimShell.scala 40:24:@128.4]
  wire  host_axi_io_axi_aw_ready; // @[SimShell.scala 40:24:@128.4]
  wire  host_axi_io_axi_aw_valid; // @[SimShell.scala 40:24:@128.4]
  wire [15:0] host_axi_io_axi_aw_bits_addr; // @[SimShell.scala 40:24:@128.4]
  wire  host_axi_io_axi_w_ready; // @[SimShell.scala 40:24:@128.4]
  wire  host_axi_io_axi_w_valid; // @[SimShell.scala 40:24:@128.4]
  wire [31:0] host_axi_io_axi_w_bits_data; // @[SimShell.scala 40:24:@128.4]
  wire  host_axi_io_axi_b_ready; // @[SimShell.scala 40:24:@128.4]
  wire  host_axi_io_axi_b_valid; // @[SimShell.scala 40:24:@128.4]
  wire  host_axi_io_axi_ar_ready; // @[SimShell.scala 40:24:@128.4]
  wire  host_axi_io_axi_ar_valid; // @[SimShell.scala 40:24:@128.4]
  wire [15:0] host_axi_io_axi_ar_bits_addr; // @[SimShell.scala 40:24:@128.4]
  wire  host_axi_io_axi_r_ready; // @[SimShell.scala 40:24:@128.4]
  wire  host_axi_io_axi_r_valid; // @[SimShell.scala 40:24:@128.4]
  wire [31:0] host_axi_io_axi_r_bits_data; // @[SimShell.scala 40:24:@128.4]
  VTAHostDPI host_dpi ( // @[SimShell.scala 39:24:@124.4]
    .dpi_req_valid(host_dpi_dpi_req_valid),
    .dpi_req_opcode(host_dpi_dpi_req_opcode),
    .dpi_req_addr(host_dpi_dpi_req_addr),
    .dpi_req_value(host_dpi_dpi_req_value),
    .dpi_req_deq(host_dpi_dpi_req_deq),
    .dpi_resp_valid(host_dpi_dpi_resp_valid),
    .dpi_resp_bits(host_dpi_dpi_resp_bits),
    .reset(host_dpi_reset),
    .clock(host_dpi_clock)
  );
  VTAHostDPIToAXI host_axi ( // @[SimShell.scala 40:24:@128.4]
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
  assign io_axi_aw_valid = host_axi_io_axi_aw_valid; // @[SimShell.scala 44:10:@155.4]
  assign io_axi_aw_bits_addr = host_axi_io_axi_aw_bits_addr; // @[SimShell.scala 44:10:@154.4]
  assign io_axi_w_valid = host_axi_io_axi_w_valid; // @[SimShell.scala 44:10:@152.4]
  assign io_axi_w_bits_data = host_axi_io_axi_w_bits_data; // @[SimShell.scala 44:10:@151.4]
  assign io_axi_b_ready = host_axi_io_axi_b_ready; // @[SimShell.scala 44:10:@149.4]
  assign io_axi_ar_valid = host_axi_io_axi_ar_valid; // @[SimShell.scala 44:10:@145.4]
  assign io_axi_ar_bits_addr = host_axi_io_axi_ar_bits_addr; // @[SimShell.scala 44:10:@144.4]
  assign io_axi_r_ready = host_axi_io_axi_r_ready; // @[SimShell.scala 44:10:@143.4]
  assign host_dpi_dpi_req_deq = host_axi_io_dpi_req_deq; // @[SimShell.scala 43:19:@135.4]
  assign host_dpi_dpi_resp_valid = host_axi_io_dpi_resp_valid; // @[SimShell.scala 43:19:@134.4]
  assign host_dpi_dpi_resp_bits = host_axi_io_dpi_resp_bits; // @[SimShell.scala 43:19:@133.4]
  assign host_dpi_reset = reset; // @[SimShell.scala 41:21:@131.4]
  assign host_dpi_clock = clock; // @[SimShell.scala 42:21:@132.4]
  assign host_axi_clock = clock; // @[:@129.4]
  assign host_axi_reset = reset; // @[:@130.4]
  assign host_axi_io_dpi_req_valid = host_dpi_dpi_req_valid; // @[SimShell.scala 43:19:@139.4]
  assign host_axi_io_dpi_req_opcode = host_dpi_dpi_req_opcode; // @[SimShell.scala 43:19:@138.4]
  assign host_axi_io_dpi_req_addr = host_dpi_dpi_req_addr; // @[SimShell.scala 43:19:@137.4]
  assign host_axi_io_dpi_req_value = host_dpi_dpi_req_value; // @[SimShell.scala 43:19:@136.4]
  assign host_axi_io_axi_aw_ready = io_axi_aw_ready; // @[SimShell.scala 44:10:@156.4]
  assign host_axi_io_axi_w_ready = io_axi_w_ready; // @[SimShell.scala 44:10:@153.4]
  assign host_axi_io_axi_b_valid = io_axi_b_valid; // @[SimShell.scala 44:10:@148.4]
  assign host_axi_io_axi_ar_ready = io_axi_ar_ready; // @[SimShell.scala 44:10:@146.4]
  assign host_axi_io_axi_r_valid = io_axi_r_valid; // @[SimShell.scala 44:10:@142.4]
  assign host_axi_io_axi_r_bits_data = io_axi_r_bits_data; // @[SimShell.scala 44:10:@141.4]
endmodule
module VTAMemDPIToAXI( // @[:@166.2]
  input         clock, // @[:@167.4]
  input         reset, // @[:@168.4]
  output        io_dpi_req_valid, // @[:@169.4]
  output        io_dpi_req_opcode, // @[:@169.4]
  output [7:0]  io_dpi_req_len, // @[:@169.4]
  output [63:0] io_dpi_req_addr, // @[:@169.4]
  output        io_dpi_wr_valid, // @[:@169.4]
  output [63:0] io_dpi_wr_bits, // @[:@169.4]
  output        io_dpi_rd_ready, // @[:@169.4]
  input         io_dpi_rd_valid, // @[:@169.4]
  input  [63:0] io_dpi_rd_bits, // @[:@169.4]
  output        io_axi_aw_ready, // @[:@169.4]
  input         io_axi_aw_valid, // @[:@169.4]
  input  [31:0] io_axi_aw_bits_addr, // @[:@169.4]
  input  [3:0]  io_axi_aw_bits_len, // @[:@169.4]
  output        io_axi_w_ready, // @[:@169.4]
  input         io_axi_w_valid, // @[:@169.4]
  input  [63:0] io_axi_w_bits_data, // @[:@169.4]
  input         io_axi_w_bits_last, // @[:@169.4]
  input         io_axi_b_ready, // @[:@169.4]
  output        io_axi_b_valid, // @[:@169.4]
  output        io_axi_ar_ready, // @[:@169.4]
  input         io_axi_ar_valid, // @[:@169.4]
  input  [31:0] io_axi_ar_bits_addr, // @[:@169.4]
  input  [3:0]  io_axi_ar_bits_len, // @[:@169.4]
  input         io_axi_r_ready, // @[:@169.4]
  output        io_axi_r_valid, // @[:@169.4]
  output [63:0] io_axi_r_bits_data, // @[:@169.4]
  output        io_axi_r_bits_last // @[:@169.4]
);
  reg  opcode; // @[VTAMemDPI.scala 83:23:@171.4]
  reg [31:0] _RAND_0;
  reg [7:0] len; // @[VTAMemDPI.scala 84:20:@174.4]
  reg [31:0] _RAND_1;
  reg [63:0] addr; // @[VTAMemDPI.scala 85:21:@177.4]
  reg [63:0] _RAND_2;
  reg [2:0] state; // @[VTAMemDPI.scala 87:22:@178.4]
  reg [31:0] _RAND_3;
  wire  _T_90; // @[Conditional.scala 37:30:@179.4]
  wire [2:0] _GEN_0; // @[VTAMemDPI.scala 93:37:@185.8]
  wire [2:0] _GEN_1; // @[VTAMemDPI.scala 91:30:@181.6]
  wire  _T_91; // @[Conditional.scala 37:30:@190.6]
  wire [2:0] _GEN_2; // @[VTAMemDPI.scala 98:30:@192.8]
  wire  _T_92; // @[Conditional.scala 37:30:@197.8]
  wire  _T_93; // @[VTAMemDPI.scala 103:28:@199.10]
  wire  _T_95; // @[VTAMemDPI.scala 103:54:@200.10]
  wire  _T_96; // @[VTAMemDPI.scala 103:47:@201.10]
  wire [2:0] _GEN_3; // @[VTAMemDPI.scala 103:63:@202.10]
  wire  _T_97; // @[Conditional.scala 37:30:@207.10]
  wire [2:0] _GEN_4; // @[VTAMemDPI.scala 108:30:@209.12]
  wire  _T_98; // @[Conditional.scala 37:30:@214.12]
  wire  _T_99; // @[VTAMemDPI.scala 113:28:@216.14]
  wire [2:0] _GEN_5; // @[VTAMemDPI.scala 113:51:@217.14]
  wire  _T_100; // @[Conditional.scala 37:30:@222.14]
  wire [2:0] _GEN_6; // @[VTAMemDPI.scala 118:29:@224.16]
  wire [2:0] _GEN_7; // @[Conditional.scala 39:67:@223.14]
  wire [2:0] _GEN_8; // @[Conditional.scala 39:67:@215.12]
  wire [2:0] _GEN_9; // @[Conditional.scala 39:67:@208.10]
  wire [2:0] _GEN_10; // @[Conditional.scala 39:67:@198.8]
  wire [2:0] _GEN_11; // @[Conditional.scala 39:67:@191.6]
  wire [2:0] _GEN_12; // @[Conditional.scala 40:58:@180.4]
  wire  _T_101; // @[VTAMemDPI.scala 124:15:@228.4]
  wire  _GEN_13; // @[VTAMemDPI.scala 129:35:@236.8]
  wire [7:0] _GEN_14; // @[VTAMemDPI.scala 129:35:@236.8]
  wire [63:0] _GEN_15; // @[VTAMemDPI.scala 129:35:@236.8]
  wire  _GEN_16; // @[VTAMemDPI.scala 125:28:@230.6]
  wire [7:0] _GEN_17; // @[VTAMemDPI.scala 125:28:@230.6]
  wire [63:0] _GEN_18; // @[VTAMemDPI.scala 125:28:@230.6]
  wire  _T_104; // @[VTAMemDPI.scala 134:22:@243.6]
  wire  _T_107; // @[VTAMemDPI.scala 135:52:@246.8]
  wire  _T_108; // @[VTAMemDPI.scala 135:45:@247.8]
  wire [8:0] _T_110; // @[VTAMemDPI.scala 136:18:@249.10]
  wire [8:0] _T_111; // @[VTAMemDPI.scala 136:18:@250.10]
  wire [7:0] _T_112; // @[VTAMemDPI.scala 136:18:@251.10]
  wire [7:0] _GEN_19; // @[VTAMemDPI.scala 135:61:@248.8]
  wire [7:0] _GEN_20; // @[VTAMemDPI.scala 134:37:@244.6]
  wire  _GEN_21; // @[VTAMemDPI.scala 124:26:@229.4]
  wire [7:0] _GEN_22; // @[VTAMemDPI.scala 124:26:@229.4]
  wire [63:0] _GEN_23; // @[VTAMemDPI.scala 124:26:@229.4]
  wire  _T_113; // @[VTAMemDPI.scala 140:30:@255.4]
  wire  _T_114; // @[VTAMemDPI.scala 140:47:@256.4]
  wire  _T_115; // @[VTAMemDPI.scala 140:75:@257.4]
  wire  _T_116; // @[VTAMemDPI.scala 140:93:@258.4]
  wire  _T_129; // @[VTAMemDPI.scala 156:28:@280.4]
  assign _T_90 = 3'h0 == state; // @[Conditional.scala 37:30:@179.4]
  assign _GEN_0 = io_axi_aw_valid ? 3'h3 : state; // @[VTAMemDPI.scala 93:37:@185.8]
  assign _GEN_1 = io_axi_ar_valid ? 3'h1 : _GEN_0; // @[VTAMemDPI.scala 91:30:@181.6]
  assign _T_91 = 3'h1 == state; // @[Conditional.scala 37:30:@190.6]
  assign _GEN_2 = io_axi_ar_valid ? 3'h2 : state; // @[VTAMemDPI.scala 98:30:@192.8]
  assign _T_92 = 3'h2 == state; // @[Conditional.scala 37:30:@197.8]
  assign _T_93 = io_axi_r_ready & io_dpi_rd_valid; // @[VTAMemDPI.scala 103:28:@199.10]
  assign _T_95 = len == 8'h0; // @[VTAMemDPI.scala 103:54:@200.10]
  assign _T_96 = _T_93 & _T_95; // @[VTAMemDPI.scala 103:47:@201.10]
  assign _GEN_3 = _T_96 ? 3'h0 : state; // @[VTAMemDPI.scala 103:63:@202.10]
  assign _T_97 = 3'h3 == state; // @[Conditional.scala 37:30:@207.10]
  assign _GEN_4 = io_axi_aw_valid ? 3'h4 : state; // @[VTAMemDPI.scala 108:30:@209.12]
  assign _T_98 = 3'h4 == state; // @[Conditional.scala 37:30:@214.12]
  assign _T_99 = io_axi_w_valid & io_axi_w_bits_last; // @[VTAMemDPI.scala 113:28:@216.14]
  assign _GEN_5 = _T_99 ? 3'h5 : state; // @[VTAMemDPI.scala 113:51:@217.14]
  assign _T_100 = 3'h5 == state; // @[Conditional.scala 37:30:@222.14]
  assign _GEN_6 = io_axi_b_ready ? 3'h0 : state; // @[VTAMemDPI.scala 118:29:@224.16]
  assign _GEN_7 = _T_100 ? _GEN_6 : state; // @[Conditional.scala 39:67:@223.14]
  assign _GEN_8 = _T_98 ? _GEN_5 : _GEN_7; // @[Conditional.scala 39:67:@215.12]
  assign _GEN_9 = _T_97 ? _GEN_4 : _GEN_8; // @[Conditional.scala 39:67:@208.10]
  assign _GEN_10 = _T_92 ? _GEN_3 : _GEN_9; // @[Conditional.scala 39:67:@198.8]
  assign _GEN_11 = _T_91 ? _GEN_2 : _GEN_10; // @[Conditional.scala 39:67:@191.6]
  assign _GEN_12 = _T_90 ? _GEN_1 : _GEN_11; // @[Conditional.scala 40:58:@180.4]
  assign _T_101 = state == 3'h0; // @[VTAMemDPI.scala 124:15:@228.4]
  assign _GEN_13 = io_axi_aw_valid ? 1'h1 : opcode; // @[VTAMemDPI.scala 129:35:@236.8]
  assign _GEN_14 = io_axi_aw_valid ? {{4'd0}, io_axi_aw_bits_len} : len; // @[VTAMemDPI.scala 129:35:@236.8]
  assign _GEN_15 = io_axi_aw_valid ? {{32'd0}, io_axi_aw_bits_addr} : addr; // @[VTAMemDPI.scala 129:35:@236.8]
  assign _GEN_16 = io_axi_ar_valid ? 1'h0 : _GEN_13; // @[VTAMemDPI.scala 125:28:@230.6]
  assign _GEN_17 = io_axi_ar_valid ? {{4'd0}, io_axi_ar_bits_len} : _GEN_14; // @[VTAMemDPI.scala 125:28:@230.6]
  assign _GEN_18 = io_axi_ar_valid ? {{32'd0}, io_axi_ar_bits_addr} : _GEN_15; // @[VTAMemDPI.scala 125:28:@230.6]
  assign _T_104 = state == 3'h2; // @[VTAMemDPI.scala 134:22:@243.6]
  assign _T_107 = len != 8'h0; // @[VTAMemDPI.scala 135:52:@246.8]
  assign _T_108 = _T_93 & _T_107; // @[VTAMemDPI.scala 135:45:@247.8]
  assign _T_110 = len - 8'h1; // @[VTAMemDPI.scala 136:18:@249.10]
  assign _T_111 = $unsigned(_T_110); // @[VTAMemDPI.scala 136:18:@250.10]
  assign _T_112 = _T_111[7:0]; // @[VTAMemDPI.scala 136:18:@251.10]
  assign _GEN_19 = _T_108 ? _T_112 : len; // @[VTAMemDPI.scala 135:61:@248.8]
  assign _GEN_20 = _T_104 ? _GEN_19 : len; // @[VTAMemDPI.scala 134:37:@244.6]
  assign _GEN_21 = _T_101 ? _GEN_16 : opcode; // @[VTAMemDPI.scala 124:26:@229.4]
  assign _GEN_22 = _T_101 ? _GEN_17 : _GEN_20; // @[VTAMemDPI.scala 124:26:@229.4]
  assign _GEN_23 = _T_101 ? _GEN_18 : addr; // @[VTAMemDPI.scala 124:26:@229.4]
  assign _T_113 = state == 3'h1; // @[VTAMemDPI.scala 140:30:@255.4]
  assign _T_114 = _T_113 & io_axi_ar_valid; // @[VTAMemDPI.scala 140:47:@256.4]
  assign _T_115 = state == 3'h3; // @[VTAMemDPI.scala 140:75:@257.4]
  assign _T_116 = _T_115 & io_axi_aw_valid; // @[VTAMemDPI.scala 140:93:@258.4]
  assign _T_129 = state == 3'h4; // @[VTAMemDPI.scala 156:28:@280.4]
  assign io_dpi_req_valid = _T_114 | _T_116; // @[VTAMemDPI.scala 140:20:@260.4]
  assign io_dpi_req_opcode = opcode; // @[VTAMemDPI.scala 141:21:@261.4]
  assign io_dpi_req_len = len; // @[VTAMemDPI.scala 142:18:@262.4]
  assign io_dpi_req_addr = addr; // @[VTAMemDPI.scala 143:19:@263.4]
  assign io_dpi_wr_valid = _T_129 & io_axi_w_valid; // @[VTAMemDPI.scala 156:19:@282.4]
  assign io_dpi_wr_bits = io_axi_w_bits_data; // @[VTAMemDPI.scala 157:18:@283.4]
  assign io_dpi_rd_ready = _T_104 & io_axi_r_ready; // @[VTAMemDPI.scala 154:19:@279.4]
  assign io_axi_aw_ready = state == 3'h3; // @[VTAMemDPI.scala 146:19:@267.4]
  assign io_axi_w_ready = state == 3'h4; // @[VTAMemDPI.scala 158:18:@285.4]
  assign io_axi_b_valid = state == 3'h5; // @[VTAMemDPI.scala 160:18:@287.4]
  assign io_axi_ar_ready = state == 3'h1; // @[VTAMemDPI.scala 145:19:@265.4]
  assign io_axi_r_valid = _T_104 & io_dpi_rd_valid; // @[VTAMemDPI.scala 148:18:@270.4]
  assign io_axi_r_bits_data = io_dpi_rd_bits; // @[VTAMemDPI.scala 149:22:@271.4]
  assign io_axi_r_bits_last = len == 8'h0; // @[VTAMemDPI.scala 150:22:@273.4]
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
`ifdef RANDOMIZE
  integer initvar;
  initial begin
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      #0.002 begin end
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
  end
`endif // RANDOMIZE
  always @(posedge clock) begin
    if (reset) begin
      opcode <= 1'h0;
    end else begin
      if (_T_101) begin
        if (io_axi_ar_valid) begin
          opcode <= 1'h0;
        end else begin
          if (io_axi_aw_valid) begin
            opcode <= 1'h1;
          end
        end
      end
    end
    if (reset) begin
      len <= 8'h0;
    end else begin
      if (_T_101) begin
        if (io_axi_ar_valid) begin
          len <= {{4'd0}, io_axi_ar_bits_len};
        end else begin
          if (io_axi_aw_valid) begin
            len <= {{4'd0}, io_axi_aw_bits_len};
          end
        end
      end else begin
        if (_T_104) begin
          if (_T_108) begin
            len <= _T_112;
          end
        end
      end
    end
    if (reset) begin
      addr <= 64'h0;
    end else begin
      if (_T_101) begin
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
      if (_T_90) begin
        if (io_axi_ar_valid) begin
          state <= 3'h1;
        end else begin
          if (io_axi_aw_valid) begin
            state <= 3'h3;
          end
        end
      end else begin
        if (_T_91) begin
          if (io_axi_ar_valid) begin
            state <= 3'h2;
          end
        end else begin
          if (_T_92) begin
            if (_T_96) begin
              state <= 3'h0;
            end
          end else begin
            if (_T_97) begin
              if (io_axi_aw_valid) begin
                state <= 3'h4;
              end
            end else begin
              if (_T_98) begin
                if (_T_99) begin
                  state <= 3'h5;
                end
              end else begin
                if (_T_100) begin
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
module VTAMem( // @[:@292.2]
  input         clock, // @[:@293.4]
  input         reset, // @[:@294.4]
  output        io_axi_aw_ready, // @[:@295.4]
  input         io_axi_aw_valid, // @[:@295.4]
  input  [31:0] io_axi_aw_bits_addr, // @[:@295.4]
  input  [3:0]  io_axi_aw_bits_len, // @[:@295.4]
  output        io_axi_w_ready, // @[:@295.4]
  input         io_axi_w_valid, // @[:@295.4]
  input  [63:0] io_axi_w_bits_data, // @[:@295.4]
  input         io_axi_w_bits_last, // @[:@295.4]
  input         io_axi_b_ready, // @[:@295.4]
  output        io_axi_b_valid, // @[:@295.4]
  output        io_axi_ar_ready, // @[:@295.4]
  input         io_axi_ar_valid, // @[:@295.4]
  input  [31:0] io_axi_ar_bits_addr, // @[:@295.4]
  input  [3:0]  io_axi_ar_bits_len, // @[:@295.4]
  input         io_axi_r_ready, // @[:@295.4]
  output        io_axi_r_valid, // @[:@295.4]
  output [63:0] io_axi_r_bits_data, // @[:@295.4]
  output        io_axi_r_bits_last // @[:@295.4]
);
  wire  mem_dpi_dpi_req_valid; // @[SimShell.scala 57:23:@297.4]
  wire  mem_dpi_dpi_req_opcode; // @[SimShell.scala 57:23:@297.4]
  wire [7:0] mem_dpi_dpi_req_len; // @[SimShell.scala 57:23:@297.4]
  wire [63:0] mem_dpi_dpi_req_addr; // @[SimShell.scala 57:23:@297.4]
  wire  mem_dpi_dpi_wr_valid; // @[SimShell.scala 57:23:@297.4]
  wire [63:0] mem_dpi_dpi_wr_bits; // @[SimShell.scala 57:23:@297.4]
  wire  mem_dpi_dpi_rd_ready; // @[SimShell.scala 57:23:@297.4]
  wire  mem_dpi_dpi_rd_valid; // @[SimShell.scala 57:23:@297.4]
  wire [63:0] mem_dpi_dpi_rd_bits; // @[SimShell.scala 57:23:@297.4]
  wire  mem_dpi_reset; // @[SimShell.scala 57:23:@297.4]
  wire  mem_dpi_clock; // @[SimShell.scala 57:23:@297.4]
  wire  mem_axi_clock; // @[SimShell.scala 58:23:@301.4]
  wire  mem_axi_reset; // @[SimShell.scala 58:23:@301.4]
  wire  mem_axi_io_dpi_req_valid; // @[SimShell.scala 58:23:@301.4]
  wire  mem_axi_io_dpi_req_opcode; // @[SimShell.scala 58:23:@301.4]
  wire [7:0] mem_axi_io_dpi_req_len; // @[SimShell.scala 58:23:@301.4]
  wire [63:0] mem_axi_io_dpi_req_addr; // @[SimShell.scala 58:23:@301.4]
  wire  mem_axi_io_dpi_wr_valid; // @[SimShell.scala 58:23:@301.4]
  wire [63:0] mem_axi_io_dpi_wr_bits; // @[SimShell.scala 58:23:@301.4]
  wire  mem_axi_io_dpi_rd_ready; // @[SimShell.scala 58:23:@301.4]
  wire  mem_axi_io_dpi_rd_valid; // @[SimShell.scala 58:23:@301.4]
  wire [63:0] mem_axi_io_dpi_rd_bits; // @[SimShell.scala 58:23:@301.4]
  wire  mem_axi_io_axi_aw_ready; // @[SimShell.scala 58:23:@301.4]
  wire  mem_axi_io_axi_aw_valid; // @[SimShell.scala 58:23:@301.4]
  wire [31:0] mem_axi_io_axi_aw_bits_addr; // @[SimShell.scala 58:23:@301.4]
  wire [3:0] mem_axi_io_axi_aw_bits_len; // @[SimShell.scala 58:23:@301.4]
  wire  mem_axi_io_axi_w_ready; // @[SimShell.scala 58:23:@301.4]
  wire  mem_axi_io_axi_w_valid; // @[SimShell.scala 58:23:@301.4]
  wire [63:0] mem_axi_io_axi_w_bits_data; // @[SimShell.scala 58:23:@301.4]
  wire  mem_axi_io_axi_w_bits_last; // @[SimShell.scala 58:23:@301.4]
  wire  mem_axi_io_axi_b_ready; // @[SimShell.scala 58:23:@301.4]
  wire  mem_axi_io_axi_b_valid; // @[SimShell.scala 58:23:@301.4]
  wire  mem_axi_io_axi_ar_ready; // @[SimShell.scala 58:23:@301.4]
  wire  mem_axi_io_axi_ar_valid; // @[SimShell.scala 58:23:@301.4]
  wire [31:0] mem_axi_io_axi_ar_bits_addr; // @[SimShell.scala 58:23:@301.4]
  wire [3:0] mem_axi_io_axi_ar_bits_len; // @[SimShell.scala 58:23:@301.4]
  wire  mem_axi_io_axi_r_ready; // @[SimShell.scala 58:23:@301.4]
  wire  mem_axi_io_axi_r_valid; // @[SimShell.scala 58:23:@301.4]
  wire [63:0] mem_axi_io_axi_r_bits_data; // @[SimShell.scala 58:23:@301.4]
  wire  mem_axi_io_axi_r_bits_last; // @[SimShell.scala 58:23:@301.4]
  VTAMemDPI mem_dpi ( // @[SimShell.scala 57:23:@297.4]
    .dpi_req_valid(mem_dpi_dpi_req_valid),
    .dpi_req_opcode(mem_dpi_dpi_req_opcode),
    .dpi_req_len(mem_dpi_dpi_req_len),
    .dpi_req_addr(mem_dpi_dpi_req_addr),
    .dpi_wr_valid(mem_dpi_dpi_wr_valid),
    .dpi_wr_bits(mem_dpi_dpi_wr_bits),
    .dpi_rd_ready(mem_dpi_dpi_rd_ready),
    .dpi_rd_valid(mem_dpi_dpi_rd_valid),
    .dpi_rd_bits(mem_dpi_dpi_rd_bits),
    .reset(mem_dpi_reset),
    .clock(mem_dpi_clock)
  );
  VTAMemDPIToAXI mem_axi ( // @[SimShell.scala 58:23:@301.4]
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
  assign io_axi_aw_ready = mem_axi_io_axi_aw_ready; // @[SimShell.scala 62:18:@359.4]
  assign io_axi_w_ready = mem_axi_io_axi_w_ready; // @[SimShell.scala 62:18:@346.4]
  assign io_axi_b_valid = mem_axi_io_axi_b_valid; // @[SimShell.scala 62:18:@338.4]
  assign io_axi_ar_ready = mem_axi_io_axi_ar_ready; // @[SimShell.scala 62:18:@334.4]
  assign io_axi_r_valid = mem_axi_io_axi_r_valid; // @[SimShell.scala 62:18:@320.4]
  assign io_axi_r_bits_data = mem_axi_io_axi_r_bits_data; // @[SimShell.scala 62:18:@319.4]
  assign io_axi_r_bits_last = mem_axi_io_axi_r_bits_last; // @[SimShell.scala 62:18:@317.4]
  assign mem_dpi_dpi_req_valid = mem_axi_io_dpi_req_valid; // @[SimShell.scala 61:18:@314.4]
  assign mem_dpi_dpi_req_opcode = mem_axi_io_dpi_req_opcode; // @[SimShell.scala 61:18:@313.4]
  assign mem_dpi_dpi_req_len = mem_axi_io_dpi_req_len; // @[SimShell.scala 61:18:@312.4]
  assign mem_dpi_dpi_req_addr = mem_axi_io_dpi_req_addr; // @[SimShell.scala 61:18:@311.4]
  assign mem_dpi_dpi_wr_valid = mem_axi_io_dpi_wr_valid; // @[SimShell.scala 61:18:@310.4]
  assign mem_dpi_dpi_wr_bits = mem_axi_io_dpi_wr_bits; // @[SimShell.scala 61:18:@309.4]
  assign mem_dpi_dpi_rd_ready = mem_axi_io_dpi_rd_ready; // @[SimShell.scala 61:18:@308.4]
  assign mem_dpi_reset = reset; // @[SimShell.scala 59:20:@304.4]
  assign mem_dpi_clock = clock; // @[SimShell.scala 60:20:@305.4]
  assign mem_axi_clock = clock; // @[:@302.4]
  assign mem_axi_reset = reset; // @[:@303.4]
  assign mem_axi_io_dpi_rd_valid = mem_dpi_dpi_rd_valid; // @[SimShell.scala 61:18:@307.4]
  assign mem_axi_io_dpi_rd_bits = mem_dpi_dpi_rd_bits; // @[SimShell.scala 61:18:@306.4]
  assign mem_axi_io_axi_aw_valid = io_axi_aw_valid; // @[SimShell.scala 62:18:@358.4]
  assign mem_axi_io_axi_aw_bits_addr = io_axi_aw_bits_addr; // @[SimShell.scala 62:18:@357.4]
  assign mem_axi_io_axi_aw_bits_len = io_axi_aw_bits_len; // @[SimShell.scala 62:18:@354.4]
  assign mem_axi_io_axi_w_valid = io_axi_w_valid; // @[SimShell.scala 62:18:@345.4]
  assign mem_axi_io_axi_w_bits_data = io_axi_w_bits_data; // @[SimShell.scala 62:18:@344.4]
  assign mem_axi_io_axi_w_bits_last = io_axi_w_bits_last; // @[SimShell.scala 62:18:@342.4]
  assign mem_axi_io_axi_b_ready = io_axi_b_ready; // @[SimShell.scala 62:18:@339.4]
  assign mem_axi_io_axi_ar_valid = io_axi_ar_valid; // @[SimShell.scala 62:18:@333.4]
  assign mem_axi_io_axi_ar_bits_addr = io_axi_ar_bits_addr; // @[SimShell.scala 62:18:@332.4]
  assign mem_axi_io_axi_ar_bits_len = io_axi_ar_bits_len; // @[SimShell.scala 62:18:@329.4]
  assign mem_axi_io_axi_r_ready = io_axi_r_ready; // @[SimShell.scala 62:18:@321.4]
endmodule
module AXISimShell( // @[:@361.2]
  input         clock, // @[:@362.4]
  input         reset, // @[:@363.4]
  output        mem_aw_ready, // @[:@364.4]
  input         mem_aw_valid, // @[:@364.4]
  input  [31:0] mem_aw_bits_addr, // @[:@364.4]
  input  [3:0]  mem_aw_bits_len, // @[:@364.4]
  output        mem_w_ready, // @[:@364.4]
  input         mem_w_valid, // @[:@364.4]
  input  [63:0] mem_w_bits_data, // @[:@364.4]
  input         mem_w_bits_last, // @[:@364.4]
  input         mem_b_ready, // @[:@364.4]
  output        mem_b_valid, // @[:@364.4]
  output        mem_ar_ready, // @[:@364.4]
  input         mem_ar_valid, // @[:@364.4]
  input  [31:0] mem_ar_bits_addr, // @[:@364.4]
  input  [3:0]  mem_ar_bits_len, // @[:@364.4]
  input         mem_r_ready, // @[:@364.4]
  output        mem_r_valid, // @[:@364.4]
  output [63:0] mem_r_bits_data, // @[:@364.4]
  output        mem_r_bits_last, // @[:@364.4]
  input         host_aw_ready, // @[:@365.4]
  output        host_aw_valid, // @[:@365.4]
  output [15:0] host_aw_bits_addr, // @[:@365.4]
  input         host_w_ready, // @[:@365.4]
  output        host_w_valid, // @[:@365.4]
  output [31:0] host_w_bits_data, // @[:@365.4]
  output        host_b_ready, // @[:@365.4]
  input         host_b_valid, // @[:@365.4]
  input         host_ar_ready, // @[:@365.4]
  output        host_ar_valid, // @[:@365.4]
  output [15:0] host_ar_bits_addr, // @[:@365.4]
  output        host_r_ready, // @[:@365.4]
  input         host_r_valid, // @[:@365.4]
  input  [31:0] host_r_bits_data, // @[:@365.4]
  input         sim_clock, // @[:@366.4]
  output        sim_wait // @[:@367.4]
);
  wire  mod_sim_clock; // @[SimShell.scala 90:23:@369.4]
  wire  mod_sim_reset; // @[SimShell.scala 90:23:@369.4]
  wire  mod_sim_sim_wait; // @[SimShell.scala 90:23:@369.4]
  wire  mod_host_clock; // @[SimShell.scala 91:24:@372.4]
  wire  mod_host_reset; // @[SimShell.scala 91:24:@372.4]
  wire  mod_host_io_axi_aw_ready; // @[SimShell.scala 91:24:@372.4]
  wire  mod_host_io_axi_aw_valid; // @[SimShell.scala 91:24:@372.4]
  wire [15:0] mod_host_io_axi_aw_bits_addr; // @[SimShell.scala 91:24:@372.4]
  wire  mod_host_io_axi_w_ready; // @[SimShell.scala 91:24:@372.4]
  wire  mod_host_io_axi_w_valid; // @[SimShell.scala 91:24:@372.4]
  wire [31:0] mod_host_io_axi_w_bits_data; // @[SimShell.scala 91:24:@372.4]
  wire  mod_host_io_axi_b_ready; // @[SimShell.scala 91:24:@372.4]
  wire  mod_host_io_axi_b_valid; // @[SimShell.scala 91:24:@372.4]
  wire  mod_host_io_axi_ar_ready; // @[SimShell.scala 91:24:@372.4]
  wire  mod_host_io_axi_ar_valid; // @[SimShell.scala 91:24:@372.4]
  wire [15:0] mod_host_io_axi_ar_bits_addr; // @[SimShell.scala 91:24:@372.4]
  wire  mod_host_io_axi_r_ready; // @[SimShell.scala 91:24:@372.4]
  wire  mod_host_io_axi_r_valid; // @[SimShell.scala 91:24:@372.4]
  wire [31:0] mod_host_io_axi_r_bits_data; // @[SimShell.scala 91:24:@372.4]
  wire  mod_mem_clock; // @[SimShell.scala 92:23:@375.4]
  wire  mod_mem_reset; // @[SimShell.scala 92:23:@375.4]
  wire  mod_mem_io_axi_aw_ready; // @[SimShell.scala 92:23:@375.4]
  wire  mod_mem_io_axi_aw_valid; // @[SimShell.scala 92:23:@375.4]
  wire [31:0] mod_mem_io_axi_aw_bits_addr; // @[SimShell.scala 92:23:@375.4]
  wire [3:0] mod_mem_io_axi_aw_bits_len; // @[SimShell.scala 92:23:@375.4]
  wire  mod_mem_io_axi_w_ready; // @[SimShell.scala 92:23:@375.4]
  wire  mod_mem_io_axi_w_valid; // @[SimShell.scala 92:23:@375.4]
  wire [63:0] mod_mem_io_axi_w_bits_data; // @[SimShell.scala 92:23:@375.4]
  wire  mod_mem_io_axi_w_bits_last; // @[SimShell.scala 92:23:@375.4]
  wire  mod_mem_io_axi_b_ready; // @[SimShell.scala 92:23:@375.4]
  wire  mod_mem_io_axi_b_valid; // @[SimShell.scala 92:23:@375.4]
  wire  mod_mem_io_axi_ar_ready; // @[SimShell.scala 92:23:@375.4]
  wire  mod_mem_io_axi_ar_valid; // @[SimShell.scala 92:23:@375.4]
  wire [31:0] mod_mem_io_axi_ar_bits_addr; // @[SimShell.scala 92:23:@375.4]
  wire [3:0] mod_mem_io_axi_ar_bits_len; // @[SimShell.scala 92:23:@375.4]
  wire  mod_mem_io_axi_r_ready; // @[SimShell.scala 92:23:@375.4]
  wire  mod_mem_io_axi_r_valid; // @[SimShell.scala 92:23:@375.4]
  wire [63:0] mod_mem_io_axi_r_bits_data; // @[SimShell.scala 92:23:@375.4]
  wire  mod_mem_io_axi_r_bits_last; // @[SimShell.scala 92:23:@375.4]
  VTASim mod_sim ( // @[SimShell.scala 90:23:@369.4]
    .clock(mod_sim_clock),
    .reset(mod_sim_reset),
    .sim_wait(mod_sim_sim_wait)
  );
  VTAHost mod_host ( // @[SimShell.scala 91:24:@372.4]
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
  VTAMem mod_mem ( // @[SimShell.scala 92:23:@375.4]
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
  assign mem_aw_ready = mod_mem_io_axi_aw_ready; // @[SimShell.scala 93:7:@422.4]
  assign mem_w_ready = mod_mem_io_axi_w_ready; // @[SimShell.scala 93:7:@409.4]
  assign mem_b_valid = mod_mem_io_axi_b_valid; // @[SimShell.scala 93:7:@401.4]
  assign mem_ar_ready = mod_mem_io_axi_ar_ready; // @[SimShell.scala 93:7:@397.4]
  assign mem_r_valid = mod_mem_io_axi_r_valid; // @[SimShell.scala 93:7:@383.4]
  assign mem_r_bits_data = mod_mem_io_axi_r_bits_data; // @[SimShell.scala 93:7:@382.4]
  assign mem_r_bits_last = mod_mem_io_axi_r_bits_last; // @[SimShell.scala 93:7:@380.4]
  assign host_aw_valid = mod_host_io_axi_aw_valid; // @[SimShell.scala 94:8:@438.4]
  assign host_aw_bits_addr = mod_host_io_axi_aw_bits_addr; // @[SimShell.scala 94:8:@437.4]
  assign host_w_valid = mod_host_io_axi_w_valid; // @[SimShell.scala 94:8:@435.4]
  assign host_w_bits_data = mod_host_io_axi_w_bits_data; // @[SimShell.scala 94:8:@434.4]
  assign host_b_ready = mod_host_io_axi_b_ready; // @[SimShell.scala 94:8:@432.4]
  assign host_ar_valid = mod_host_io_axi_ar_valid; // @[SimShell.scala 94:8:@428.4]
  assign host_ar_bits_addr = mod_host_io_axi_ar_bits_addr; // @[SimShell.scala 94:8:@427.4]
  assign host_r_ready = mod_host_io_axi_r_ready; // @[SimShell.scala 94:8:@426.4]
  assign sim_wait = mod_sim_sim_wait; // @[SimShell.scala 97:12:@442.4]
  assign mod_sim_clock = sim_clock; // @[:@370.4 SimShell.scala 96:17:@441.4]
  assign mod_sim_reset = reset; // @[:@371.4 SimShell.scala 95:17:@440.4]
  assign mod_host_clock = clock; // @[:@373.4]
  assign mod_host_reset = reset; // @[:@374.4]
  assign mod_host_io_axi_aw_ready = host_aw_ready; // @[SimShell.scala 94:8:@439.4]
  assign mod_host_io_axi_w_ready = host_w_ready; // @[SimShell.scala 94:8:@436.4]
  assign mod_host_io_axi_b_valid = host_b_valid; // @[SimShell.scala 94:8:@431.4]
  assign mod_host_io_axi_ar_ready = host_ar_ready; // @[SimShell.scala 94:8:@429.4]
  assign mod_host_io_axi_r_valid = host_r_valid; // @[SimShell.scala 94:8:@425.4]
  assign mod_host_io_axi_r_bits_data = host_r_bits_data; // @[SimShell.scala 94:8:@424.4]
  assign mod_mem_clock = clock; // @[:@376.4]
  assign mod_mem_reset = reset; // @[:@377.4]
  assign mod_mem_io_axi_aw_valid = mem_aw_valid; // @[SimShell.scala 93:7:@421.4]
  assign mod_mem_io_axi_aw_bits_addr = mem_aw_bits_addr; // @[SimShell.scala 93:7:@420.4]
  assign mod_mem_io_axi_aw_bits_len = mem_aw_bits_len; // @[SimShell.scala 93:7:@417.4]
  assign mod_mem_io_axi_w_valid = mem_w_valid; // @[SimShell.scala 93:7:@408.4]
  assign mod_mem_io_axi_w_bits_data = mem_w_bits_data; // @[SimShell.scala 93:7:@407.4]
  assign mod_mem_io_axi_w_bits_last = mem_w_bits_last; // @[SimShell.scala 93:7:@405.4]
  assign mod_mem_io_axi_b_ready = mem_b_ready; // @[SimShell.scala 93:7:@402.4]
  assign mod_mem_io_axi_ar_valid = mem_ar_valid; // @[SimShell.scala 93:7:@396.4]
  assign mod_mem_io_axi_ar_bits_addr = mem_ar_bits_addr; // @[SimShell.scala 93:7:@395.4]
  assign mod_mem_io_axi_ar_bits_len = mem_ar_bits_len; // @[SimShell.scala 93:7:@392.4]
  assign mod_mem_io_axi_r_ready = mem_r_ready; // @[SimShell.scala 93:7:@384.4]
endmodule
module VCR( // @[:@444.2]
  input         clock, // @[:@445.4]
  input         reset, // @[:@446.4]
  output        io_host_aw_ready, // @[:@447.4]
  input         io_host_aw_valid, // @[:@447.4]
  input  [15:0] io_host_aw_bits_addr, // @[:@447.4]
  output        io_host_w_ready, // @[:@447.4]
  input         io_host_w_valid, // @[:@447.4]
  input  [31:0] io_host_w_bits_data, // @[:@447.4]
  input         io_host_b_ready, // @[:@447.4]
  output        io_host_b_valid, // @[:@447.4]
  output        io_host_ar_ready, // @[:@447.4]
  input         io_host_ar_valid, // @[:@447.4]
  input  [15:0] io_host_ar_bits_addr, // @[:@447.4]
  input         io_host_r_ready, // @[:@447.4]
  output        io_host_r_valid, // @[:@447.4]
  output [31:0] io_host_r_bits_data, // @[:@447.4]
  output        io_vcr_launch, // @[:@447.4]
  input         io_vcr_finish, // @[:@447.4]
  input         io_vcr_ecnt_0_valid, // @[:@447.4]
  input  [31:0] io_vcr_ecnt_0_bits, // @[:@447.4]
  output [31:0] io_vcr_vals_0, // @[:@447.4]
  output [31:0] io_vcr_vals_1, // @[:@447.4]
  output [31:0] io_vcr_ptrs_0, // @[:@447.4]
  output [31:0] io_vcr_ptrs_2 // @[:@447.4]
);
  reg [15:0] waddr; // @[VCR.scala 93:22:@449.4]
  reg [31:0] _RAND_0;
  reg [1:0] wstate; // @[VCR.scala 96:23:@450.4]
  reg [31:0] _RAND_1;
  reg  rstate; // @[VCR.scala 100:23:@451.4]
  reg [31:0] _RAND_2;
  reg [31:0] rdata; // @[VCR.scala 101:22:@452.4]
  reg [31:0] _RAND_3;
  reg [31:0] reg_0; // @[VCR.scala 107:37:@453.4]
  reg [31:0] _RAND_4;
  reg [31:0] reg_1; // @[VCR.scala 107:37:@454.4]
  reg [31:0] _RAND_5;
  reg [31:0] reg_2; // @[VCR.scala 107:37:@455.4]
  reg [31:0] _RAND_6;
  reg [31:0] reg_3; // @[VCR.scala 107:37:@456.4]
  reg [31:0] _RAND_7;
  reg [31:0] reg_4; // @[VCR.scala 107:37:@457.4]
  reg [31:0] _RAND_8;
  reg [31:0] reg_5; // @[VCR.scala 107:37:@458.4]
  reg [31:0] _RAND_9;
  reg [31:0] reg_6; // @[VCR.scala 107:37:@459.4]
  reg [31:0] _RAND_10;
  reg [31:0] reg_7; // @[VCR.scala 107:37:@460.4]
  reg [31:0] _RAND_11;
  wire  _T_135; // @[Conditional.scala 37:30:@461.4]
  wire [1:0] _GEN_0; // @[VCR.scala 116:31:@463.6]
  wire  _T_136; // @[Conditional.scala 37:30:@468.6]
  wire [1:0] _GEN_1; // @[VCR.scala 121:30:@470.8]
  wire  _T_137; // @[Conditional.scala 37:30:@475.8]
  wire [1:0] _GEN_2; // @[VCR.scala 126:30:@477.10]
  wire [1:0] _GEN_3; // @[Conditional.scala 39:67:@476.8]
  wire [1:0] _GEN_4; // @[Conditional.scala 39:67:@469.6]
  wire [1:0] _GEN_5; // @[Conditional.scala 40:58:@462.4]
  wire  _T_138; // @[Decoupled.scala 37:37:@481.4]
  wire [15:0] _GEN_6; // @[VCR.scala 132:28:@482.4]
  wire  _T_143; // @[Conditional.scala 37:30:@492.4]
  wire  _GEN_7; // @[VCR.scala 142:31:@494.6]
  wire  _GEN_8; // @[VCR.scala 147:30:@501.8]
  wire  _GEN_9; // @[Conditional.scala 39:67:@500.6]
  wire  _GEN_10; // @[Conditional.scala 40:58:@493.4]
  wire  _T_149; // @[Decoupled.scala 37:37:@515.6]
  wire  _T_151; // @[VCR.scala 160:46:@516.6]
  wire  _T_152; // @[VCR.scala 160:33:@517.6]
  wire [31:0] _GEN_11; // @[VCR.scala 160:57:@518.6]
  wire [31:0] _GEN_12; // @[VCR.scala 158:24:@511.4]
  wire  _T_155; // @[VCR.scala 167:53:@526.6]
  wire  _T_156; // @[VCR.scala 167:35:@527.6]
  wire [31:0] _GEN_13; // @[VCR.scala 167:64:@528.6]
  wire [31:0] _GEN_14; // @[VCR.scala 165:33:@521.4]
  wire  _T_159; // @[VCR.scala 173:46:@532.4]
  wire  _T_160; // @[VCR.scala 173:28:@533.4]
  wire [31:0] _GEN_15; // @[VCR.scala 173:57:@534.4]
  wire  _T_163; // @[VCR.scala 173:46:@538.4]
  wire  _T_164; // @[VCR.scala 173:28:@539.4]
  wire [31:0] _GEN_16; // @[VCR.scala 173:57:@540.4]
  wire  _T_167; // @[VCR.scala 173:46:@544.4]
  wire  _T_168; // @[VCR.scala 173:28:@545.4]
  wire [31:0] _GEN_17; // @[VCR.scala 173:57:@546.4]
  wire  _T_171; // @[VCR.scala 173:46:@550.4]
  wire  _T_172; // @[VCR.scala 173:28:@551.4]
  wire [31:0] _GEN_18; // @[VCR.scala 173:57:@552.4]
  wire  _T_175; // @[VCR.scala 173:46:@556.4]
  wire  _T_176; // @[VCR.scala 173:28:@557.4]
  wire [31:0] _GEN_19; // @[VCR.scala 173:57:@558.4]
  wire  _T_179; // @[VCR.scala 173:46:@562.4]
  wire  _T_180; // @[VCR.scala 173:28:@563.4]
  wire [31:0] _GEN_20; // @[VCR.scala 173:57:@564.4]
  wire  _T_181; // @[Decoupled.scala 37:37:@567.4]
  wire  _T_183; // @[Mux.scala 46:19:@569.6]
  wire [31:0] _T_184; // @[Mux.scala 46:16:@570.6]
  wire  _T_185; // @[Mux.scala 46:19:@571.6]
  wire [31:0] _T_186; // @[Mux.scala 46:16:@572.6]
  wire  _T_187; // @[Mux.scala 46:19:@573.6]
  wire [31:0] _T_188; // @[Mux.scala 46:16:@574.6]
  wire  _T_189; // @[Mux.scala 46:19:@575.6]
  wire [31:0] _T_190; // @[Mux.scala 46:16:@576.6]
  wire  _T_191; // @[Mux.scala 46:19:@577.6]
  wire [31:0] _T_192; // @[Mux.scala 46:16:@578.6]
  wire  _T_193; // @[Mux.scala 46:19:@579.6]
  wire [31:0] _T_194; // @[Mux.scala 46:16:@580.6]
  wire  _T_195; // @[Mux.scala 46:19:@581.6]
  wire [31:0] _T_196; // @[Mux.scala 46:16:@582.6]
  wire  _T_197; // @[Mux.scala 46:19:@583.6]
  wire [31:0] _T_198; // @[Mux.scala 46:16:@584.6]
  wire [31:0] _GEN_21; // @[VCR.scala 178:28:@568.4]
  assign _T_135 = 2'h0 == wstate; // @[Conditional.scala 37:30:@461.4]
  assign _GEN_0 = io_host_aw_valid ? 2'h1 : wstate; // @[VCR.scala 116:31:@463.6]
  assign _T_136 = 2'h1 == wstate; // @[Conditional.scala 37:30:@468.6]
  assign _GEN_1 = io_host_w_valid ? 2'h2 : wstate; // @[VCR.scala 121:30:@470.8]
  assign _T_137 = 2'h2 == wstate; // @[Conditional.scala 37:30:@475.8]
  assign _GEN_2 = io_host_b_ready ? 2'h0 : wstate; // @[VCR.scala 126:30:@477.10]
  assign _GEN_3 = _T_137 ? _GEN_2 : wstate; // @[Conditional.scala 39:67:@476.8]
  assign _GEN_4 = _T_136 ? _GEN_1 : _GEN_3; // @[Conditional.scala 39:67:@469.6]
  assign _GEN_5 = _T_135 ? _GEN_0 : _GEN_4; // @[Conditional.scala 40:58:@462.4]
  assign _T_138 = io_host_aw_ready & io_host_aw_valid; // @[Decoupled.scala 37:37:@481.4]
  assign _GEN_6 = _T_138 ? io_host_aw_bits_addr : waddr; // @[VCR.scala 132:28:@482.4]
  assign _T_143 = 1'h0 == rstate; // @[Conditional.scala 37:30:@492.4]
  assign _GEN_7 = io_host_ar_valid ? 1'h1 : rstate; // @[VCR.scala 142:31:@494.6]
  assign _GEN_8 = io_host_r_ready ? 1'h0 : rstate; // @[VCR.scala 147:30:@501.8]
  assign _GEN_9 = rstate ? _GEN_8 : rstate; // @[Conditional.scala 39:67:@500.6]
  assign _GEN_10 = _T_143 ? _GEN_7 : _GEN_9; // @[Conditional.scala 40:58:@493.4]
  assign _T_149 = io_host_w_ready & io_host_w_valid; // @[Decoupled.scala 37:37:@515.6]
  assign _T_151 = 16'h0 == waddr; // @[VCR.scala 160:46:@516.6]
  assign _T_152 = _T_149 & _T_151; // @[VCR.scala 160:33:@517.6]
  assign _GEN_11 = _T_152 ? io_host_w_bits_data : reg_0; // @[VCR.scala 160:57:@518.6]
  assign _GEN_12 = io_vcr_finish ? 32'h2 : _GEN_11; // @[VCR.scala 158:24:@511.4]
  assign _T_155 = 16'h4 == waddr; // @[VCR.scala 167:53:@526.6]
  assign _T_156 = _T_149 & _T_155; // @[VCR.scala 167:35:@527.6]
  assign _GEN_13 = _T_156 ? io_host_w_bits_data : reg_1; // @[VCR.scala 167:64:@528.6]
  assign _GEN_14 = io_vcr_ecnt_0_valid ? io_vcr_ecnt_0_bits : _GEN_13; // @[VCR.scala 165:33:@521.4]
  assign _T_159 = 16'h8 == waddr; // @[VCR.scala 173:46:@532.4]
  assign _T_160 = _T_149 & _T_159; // @[VCR.scala 173:28:@533.4]
  assign _GEN_15 = _T_160 ? io_host_w_bits_data : reg_2; // @[VCR.scala 173:57:@534.4]
  assign _T_163 = 16'hc == waddr; // @[VCR.scala 173:46:@538.4]
  assign _T_164 = _T_149 & _T_163; // @[VCR.scala 173:28:@539.4]
  assign _GEN_16 = _T_164 ? io_host_w_bits_data : reg_3; // @[VCR.scala 173:57:@540.4]
  assign _T_167 = 16'h10 == waddr; // @[VCR.scala 173:46:@544.4]
  assign _T_168 = _T_149 & _T_167; // @[VCR.scala 173:28:@545.4]
  assign _GEN_17 = _T_168 ? io_host_w_bits_data : reg_4; // @[VCR.scala 173:57:@546.4]
  assign _T_171 = 16'h14 == waddr; // @[VCR.scala 173:46:@550.4]
  assign _T_172 = _T_149 & _T_171; // @[VCR.scala 173:28:@551.4]
  assign _GEN_18 = _T_172 ? io_host_w_bits_data : reg_5; // @[VCR.scala 173:57:@552.4]
  assign _T_175 = 16'h18 == waddr; // @[VCR.scala 173:46:@556.4]
  assign _T_176 = _T_149 & _T_175; // @[VCR.scala 173:28:@557.4]
  assign _GEN_19 = _T_176 ? io_host_w_bits_data : reg_6; // @[VCR.scala 173:57:@558.4]
  assign _T_179 = 16'h1c == waddr; // @[VCR.scala 173:46:@562.4]
  assign _T_180 = _T_149 & _T_179; // @[VCR.scala 173:28:@563.4]
  assign _GEN_20 = _T_180 ? io_host_w_bits_data : reg_7; // @[VCR.scala 173:57:@564.4]
  assign _T_181 = io_host_ar_ready & io_host_ar_valid; // @[Decoupled.scala 37:37:@567.4]
  assign _T_183 = 16'h1c == io_host_ar_bits_addr; // @[Mux.scala 46:19:@569.6]
  assign _T_184 = _T_183 ? reg_7 : 32'h0; // @[Mux.scala 46:16:@570.6]
  assign _T_185 = 16'h18 == io_host_ar_bits_addr; // @[Mux.scala 46:19:@571.6]
  assign _T_186 = _T_185 ? reg_6 : _T_184; // @[Mux.scala 46:16:@572.6]
  assign _T_187 = 16'h14 == io_host_ar_bits_addr; // @[Mux.scala 46:19:@573.6]
  assign _T_188 = _T_187 ? reg_5 : _T_186; // @[Mux.scala 46:16:@574.6]
  assign _T_189 = 16'h10 == io_host_ar_bits_addr; // @[Mux.scala 46:19:@575.6]
  assign _T_190 = _T_189 ? reg_4 : _T_188; // @[Mux.scala 46:16:@576.6]
  assign _T_191 = 16'hc == io_host_ar_bits_addr; // @[Mux.scala 46:19:@577.6]
  assign _T_192 = _T_191 ? reg_3 : _T_190; // @[Mux.scala 46:16:@578.6]
  assign _T_193 = 16'h8 == io_host_ar_bits_addr; // @[Mux.scala 46:19:@579.6]
  assign _T_194 = _T_193 ? reg_2 : _T_192; // @[Mux.scala 46:16:@580.6]
  assign _T_195 = 16'h4 == io_host_ar_bits_addr; // @[Mux.scala 46:19:@581.6]
  assign _T_196 = _T_195 ? reg_1 : _T_194; // @[Mux.scala 46:16:@582.6]
  assign _T_197 = 16'h0 == io_host_ar_bits_addr; // @[Mux.scala 46:19:@583.6]
  assign _T_198 = _T_197 ? reg_0 : _T_196; // @[Mux.scala 46:16:@584.6]
  assign _GEN_21 = _T_181 ? _T_198 : rdata; // @[VCR.scala 178:28:@568.4]
  assign io_host_aw_ready = wstate == 2'h0; // @[VCR.scala 134:20:@486.4]
  assign io_host_w_ready = wstate == 2'h1; // @[VCR.scala 135:19:@488.4]
  assign io_host_b_valid = wstate == 2'h2; // @[VCR.scala 136:19:@490.4]
  assign io_host_ar_ready = rstate == 1'h0; // @[VCR.scala 153:20:@506.4]
  assign io_host_r_valid = rstate; // @[VCR.scala 154:19:@508.4]
  assign io_host_r_bits_data = rdata; // @[VCR.scala 155:23:@509.4]
  assign io_vcr_launch = reg_0[0]; // @[VCR.scala 182:17:@588.4]
  assign io_vcr_vals_0 = reg_2; // @[VCR.scala 185:20:@589.4]
  assign io_vcr_vals_1 = reg_3; // @[VCR.scala 185:20:@590.4]
  assign io_vcr_ptrs_0 = reg_4; // @[VCR.scala 190:22:@591.4]
  assign io_vcr_ptrs_2 = reg_6; // @[VCR.scala 190:22:@593.4]
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
`ifdef RANDOMIZE
  integer initvar;
  initial begin
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      #0.002 begin end
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  waddr = _RAND_0[15:0];
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
  end
`endif // RANDOMIZE
  always @(posedge clock) begin
    if (reset) begin
      waddr <= 16'hffff;
    end else begin
      if (_T_138) begin
        waddr <= io_host_aw_bits_addr;
      end
    end
    if (reset) begin
      wstate <= 2'h0;
    end else begin
      if (_T_135) begin
        if (io_host_aw_valid) begin
          wstate <= 2'h1;
        end
      end else begin
        if (_T_136) begin
          if (io_host_w_valid) begin
            wstate <= 2'h2;
          end
        end else begin
          if (_T_137) begin
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
      if (_T_143) begin
        if (io_host_ar_valid) begin
          rstate <= 1'h1;
        end
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
      if (_T_181) begin
        if (_T_197) begin
          rdata <= reg_0;
        end else begin
          if (_T_195) begin
            rdata <= reg_1;
          end else begin
            if (_T_193) begin
              rdata <= reg_2;
            end else begin
              if (_T_191) begin
                rdata <= reg_3;
              end else begin
                if (_T_189) begin
                  rdata <= reg_4;
                end else begin
                  if (_T_187) begin
                    rdata <= reg_5;
                  end else begin
                    if (_T_185) begin
                      rdata <= reg_6;
                    end else begin
                      if (_T_183) begin
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
        if (_T_152) begin
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
        if (_T_156) begin
          reg_1 <= io_host_w_bits_data;
        end
      end
    end
    if (reset) begin
      reg_2 <= 32'h0;
    end else begin
      if (_T_160) begin
        reg_2 <= io_host_w_bits_data;
      end
    end
    if (reset) begin
      reg_3 <= 32'h0;
    end else begin
      if (_T_164) begin
        reg_3 <= io_host_w_bits_data;
      end
    end
    if (reset) begin
      reg_4 <= 32'h0;
    end else begin
      if (_T_168) begin
        reg_4 <= io_host_w_bits_data;
      end
    end
    if (reset) begin
      reg_5 <= 32'h0;
    end else begin
      if (_T_172) begin
        reg_5 <= io_host_w_bits_data;
      end
    end
    if (reset) begin
      reg_6 <= 32'h0;
    end else begin
      if (_T_176) begin
        reg_6 <= io_host_w_bits_data;
      end
    end
    if (reset) begin
      reg_7 <= 32'h0;
    end else begin
      if (_T_180) begin
        reg_7 <= io_host_w_bits_data;
      end
    end
  end
endmodule
module Arbiter( // @[:@596.2]
  output        io_in_0_ready, // @[:@599.4]
  input         io_in_0_valid, // @[:@599.4]
  input  [31:0] io_in_0_bits_addr, // @[:@599.4]
  input  [3:0]  io_in_0_bits_len, // @[:@599.4]
  input         io_out_ready, // @[:@599.4]
  output        io_out_valid, // @[:@599.4]
  output [31:0] io_out_bits_addr, // @[:@599.4]
  output [3:0]  io_out_bits_len // @[:@599.4]
);
  assign io_in_0_ready = io_out_ready; // @[Arbiter.scala 134:14:@605.4]
  assign io_out_valid = io_in_0_valid; // @[Arbiter.scala 135:16:@608.4]
  assign io_out_bits_addr = io_in_0_bits_addr; // @[Arbiter.scala 124:15:@603.4]
  assign io_out_bits_len = io_in_0_bits_len; // @[Arbiter.scala 124:15:@602.4]
endmodule
module VME( // @[:@610.2]
  input         clock, // @[:@611.4]
  input         reset, // @[:@612.4]
  input         io_mem_aw_ready, // @[:@613.4]
  output        io_mem_aw_valid, // @[:@613.4]
  output [31:0] io_mem_aw_bits_addr, // @[:@613.4]
  output [3:0]  io_mem_aw_bits_len, // @[:@613.4]
  input         io_mem_w_ready, // @[:@613.4]
  output        io_mem_w_valid, // @[:@613.4]
  output [63:0] io_mem_w_bits_data, // @[:@613.4]
  output        io_mem_w_bits_last, // @[:@613.4]
  output        io_mem_b_ready, // @[:@613.4]
  input         io_mem_b_valid, // @[:@613.4]
  input         io_mem_ar_ready, // @[:@613.4]
  output        io_mem_ar_valid, // @[:@613.4]
  output [31:0] io_mem_ar_bits_addr, // @[:@613.4]
  output [3:0]  io_mem_ar_bits_len, // @[:@613.4]
  output        io_mem_r_ready, // @[:@613.4]
  input         io_mem_r_valid, // @[:@613.4]
  input  [63:0] io_mem_r_bits_data, // @[:@613.4]
  input         io_mem_r_bits_last, // @[:@613.4]
  output        io_vme_rd_0_cmd_ready, // @[:@613.4]
  input         io_vme_rd_0_cmd_valid, // @[:@613.4]
  input  [31:0] io_vme_rd_0_cmd_bits_addr, // @[:@613.4]
  input  [3:0]  io_vme_rd_0_cmd_bits_len, // @[:@613.4]
  input         io_vme_rd_0_data_ready, // @[:@613.4]
  output        io_vme_rd_0_data_valid, // @[:@613.4]
  output [63:0] io_vme_rd_0_data_bits, // @[:@613.4]
  output        io_vme_wr_0_cmd_ready, // @[:@613.4]
  input         io_vme_wr_0_cmd_valid, // @[:@613.4]
  input  [31:0] io_vme_wr_0_cmd_bits_addr, // @[:@613.4]
  input  [3:0]  io_vme_wr_0_cmd_bits_len, // @[:@613.4]
  output        io_vme_wr_0_data_ready, // @[:@613.4]
  input         io_vme_wr_0_data_valid, // @[:@613.4]
  input  [63:0] io_vme_wr_0_data_bits, // @[:@613.4]
  output        io_vme_wr_0_ack // @[:@613.4]
);
  wire  rd_arb_io_in_0_ready; // @[VME.scala 144:22:@615.4]
  wire  rd_arb_io_in_0_valid; // @[VME.scala 144:22:@615.4]
  wire [31:0] rd_arb_io_in_0_bits_addr; // @[VME.scala 144:22:@615.4]
  wire [3:0] rd_arb_io_in_0_bits_len; // @[VME.scala 144:22:@615.4]
  wire  rd_arb_io_out_ready; // @[VME.scala 144:22:@615.4]
  wire  rd_arb_io_out_valid; // @[VME.scala 144:22:@615.4]
  wire [31:0] rd_arb_io_out_bits_addr; // @[VME.scala 144:22:@615.4]
  wire [3:0] rd_arb_io_out_bits_len; // @[VME.scala 144:22:@615.4]
  wire  _T_188; // @[Decoupled.scala 37:37:@618.4]
  reg [1:0] rstate; // @[VME.scala 150:23:@627.4]
  reg [31:0] _RAND_0;
  wire  _T_191; // @[Conditional.scala 37:30:@628.4]
  wire [1:0] _GEN_1; // @[VME.scala 154:34:@630.6]
  wire  _T_192; // @[Conditional.scala 37:30:@635.6]
  wire [1:0] _GEN_2; // @[VME.scala 159:30:@637.8]
  wire  _T_193; // @[Conditional.scala 37:30:@642.8]
  wire  _T_194; // @[Decoupled.scala 37:37:@644.10]
  wire  _T_195; // @[VME.scala 164:29:@645.10]
  wire [1:0] _GEN_3; // @[VME.scala 164:52:@646.10]
  wire [1:0] _GEN_4; // @[Conditional.scala 39:67:@643.8]
  wire [1:0] _GEN_5; // @[Conditional.scala 39:67:@636.6]
  wire [1:0] _GEN_6; // @[Conditional.scala 40:58:@629.4]
  reg [1:0] wstate; // @[VME.scala 171:23:@650.4]
  reg [31:0] _RAND_1;
  reg [3:0] wr_cnt; // @[VME.scala 174:23:@651.4]
  reg [31:0] _RAND_2;
  wire  _T_199; // @[VME.scala 176:16:@652.4]
  wire  _T_201; // @[Decoupled.scala 37:37:@657.6]
  wire [4:0] _T_203; // @[VME.scala 179:22:@659.8]
  wire [3:0] _T_204; // @[VME.scala 179:22:@660.8]
  wire [3:0] _GEN_7; // @[VME.scala 178:33:@658.6]
  wire [3:0] _GEN_8; // @[VME.scala 176:32:@653.4]
  wire  _T_205; // @[Conditional.scala 37:30:@663.4]
  wire [1:0] _GEN_9; // @[VME.scala 184:37:@665.6]
  wire  _T_206; // @[Conditional.scala 37:30:@670.6]
  wire [1:0] _GEN_10; // @[VME.scala 189:30:@672.8]
  wire  _T_207; // @[Conditional.scala 37:30:@677.8]
  wire  _T_208; // @[VME.scala 194:37:@679.10]
  wire  _T_209; // @[VME.scala 194:65:@680.10]
  wire  _T_210; // @[VME.scala 194:55:@681.10]
  wire [1:0] _GEN_11; // @[VME.scala 194:96:@682.10]
  wire  _T_211; // @[Conditional.scala 37:30:@687.10]
  wire [1:0] _GEN_12; // @[VME.scala 199:29:@689.12]
  wire [1:0] _GEN_13; // @[Conditional.scala 39:67:@688.10]
  wire [1:0] _GEN_14; // @[Conditional.scala 39:67:@678.8]
  wire [1:0] _GEN_15; // @[Conditional.scala 39:67:@671.6]
  wire [1:0] _GEN_16; // @[Conditional.scala 40:58:@664.4]
  reg [3:0] rd_len; // @[VME.scala 207:23:@693.4]
  reg [31:0] _RAND_3;
  reg [3:0] wr_len; // @[VME.scala 208:23:@694.4]
  reg [31:0] _RAND_4;
  reg [31:0] rd_addr; // @[VME.scala 209:24:@695.4]
  reg [31:0] _RAND_5;
  reg [31:0] wr_addr; // @[VME.scala 210:24:@696.4]
  reg [31:0] _RAND_6;
  wire [3:0] _GEN_17; // @[VME.scala 212:31:@698.4]
  wire [31:0] _GEN_18; // @[VME.scala 212:31:@698.4]
  wire  _T_221; // @[Decoupled.scala 37:37:@702.4]
  wire [3:0] _GEN_19; // @[VME.scala 217:34:@703.4]
  wire [31:0] _GEN_20; // @[VME.scala 217:34:@703.4]
  wire  _T_228; // @[VME.scala 233:37:@717.4]
  wire  _T_236; // @[VME.scala 250:28:@736.4]
  Arbiter rd_arb ( // @[VME.scala 144:22:@615.4]
    .io_in_0_ready(rd_arb_io_in_0_ready),
    .io_in_0_valid(rd_arb_io_in_0_valid),
    .io_in_0_bits_addr(rd_arb_io_in_0_bits_addr),
    .io_in_0_bits_len(rd_arb_io_in_0_bits_len),
    .io_out_ready(rd_arb_io_out_ready),
    .io_out_valid(rd_arb_io_out_valid),
    .io_out_bits_addr(rd_arb_io_out_bits_addr),
    .io_out_bits_len(rd_arb_io_out_bits_len)
  );
  assign _T_188 = rd_arb_io_out_ready & rd_arb_io_out_valid; // @[Decoupled.scala 37:37:@618.4]
  assign _T_191 = 2'h0 == rstate; // @[Conditional.scala 37:30:@628.4]
  assign _GEN_1 = rd_arb_io_out_valid ? 2'h1 : rstate; // @[VME.scala 154:34:@630.6]
  assign _T_192 = 2'h1 == rstate; // @[Conditional.scala 37:30:@635.6]
  assign _GEN_2 = io_mem_ar_ready ? 2'h2 : rstate; // @[VME.scala 159:30:@637.8]
  assign _T_193 = 2'h2 == rstate; // @[Conditional.scala 37:30:@642.8]
  assign _T_194 = io_mem_r_ready & io_mem_r_valid; // @[Decoupled.scala 37:37:@644.10]
  assign _T_195 = _T_194 & io_mem_r_bits_last; // @[VME.scala 164:29:@645.10]
  assign _GEN_3 = _T_195 ? 2'h0 : rstate; // @[VME.scala 164:52:@646.10]
  assign _GEN_4 = _T_193 ? _GEN_3 : rstate; // @[Conditional.scala 39:67:@643.8]
  assign _GEN_5 = _T_192 ? _GEN_2 : _GEN_4; // @[Conditional.scala 39:67:@636.6]
  assign _GEN_6 = _T_191 ? _GEN_1 : _GEN_5; // @[Conditional.scala 40:58:@629.4]
  assign _T_199 = wstate == 2'h0; // @[VME.scala 176:16:@652.4]
  assign _T_201 = io_mem_w_ready & io_mem_w_valid; // @[Decoupled.scala 37:37:@657.6]
  assign _T_203 = wr_cnt + 4'h1; // @[VME.scala 179:22:@659.8]
  assign _T_204 = wr_cnt + 4'h1; // @[VME.scala 179:22:@660.8]
  assign _GEN_7 = _T_201 ? _T_204 : wr_cnt; // @[VME.scala 178:33:@658.6]
  assign _GEN_8 = _T_199 ? 4'h0 : _GEN_7; // @[VME.scala 176:32:@653.4]
  assign _T_205 = 2'h0 == wstate; // @[Conditional.scala 37:30:@663.4]
  assign _GEN_9 = io_vme_wr_0_cmd_valid ? 2'h1 : wstate; // @[VME.scala 184:37:@665.6]
  assign _T_206 = 2'h1 == wstate; // @[Conditional.scala 37:30:@670.6]
  assign _GEN_10 = io_mem_aw_ready ? 2'h2 : wstate; // @[VME.scala 189:30:@672.8]
  assign _T_207 = 2'h2 == wstate; // @[Conditional.scala 37:30:@677.8]
  assign _T_208 = io_vme_wr_0_data_valid & io_mem_w_ready; // @[VME.scala 194:37:@679.10]
  assign _T_209 = wr_cnt == io_vme_wr_0_cmd_bits_len; // @[VME.scala 194:65:@680.10]
  assign _T_210 = _T_208 & _T_209; // @[VME.scala 194:55:@681.10]
  assign _GEN_11 = _T_210 ? 2'h3 : wstate; // @[VME.scala 194:96:@682.10]
  assign _T_211 = 2'h3 == wstate; // @[Conditional.scala 37:30:@687.10]
  assign _GEN_12 = io_mem_b_valid ? 2'h0 : wstate; // @[VME.scala 199:29:@689.12]
  assign _GEN_13 = _T_211 ? _GEN_12 : wstate; // @[Conditional.scala 39:67:@688.10]
  assign _GEN_14 = _T_207 ? _GEN_11 : _GEN_13; // @[Conditional.scala 39:67:@678.8]
  assign _GEN_15 = _T_206 ? _GEN_10 : _GEN_14; // @[Conditional.scala 39:67:@671.6]
  assign _GEN_16 = _T_205 ? _GEN_9 : _GEN_15; // @[Conditional.scala 40:58:@664.4]
  assign _GEN_17 = _T_188 ? rd_arb_io_out_bits_len : rd_len; // @[VME.scala 212:31:@698.4]
  assign _GEN_18 = _T_188 ? rd_arb_io_out_bits_addr : rd_addr; // @[VME.scala 212:31:@698.4]
  assign _T_221 = io_vme_wr_0_cmd_ready & io_vme_wr_0_cmd_valid; // @[Decoupled.scala 37:37:@702.4]
  assign _GEN_19 = _T_221 ? io_vme_wr_0_cmd_bits_len : wr_len; // @[VME.scala 217:34:@703.4]
  assign _GEN_20 = _T_221 ? io_vme_wr_0_cmd_bits_addr : wr_addr; // @[VME.scala 217:34:@703.4]
  assign _T_228 = wstate == 2'h2; // @[VME.scala 233:37:@717.4]
  assign _T_236 = rstate == 2'h2; // @[VME.scala 250:28:@736.4]
  assign io_mem_aw_valid = wstate == 2'h1; // @[VME.scala 236:19:@721.4]
  assign io_mem_aw_bits_addr = wr_addr; // @[VME.scala 237:23:@722.4]
  assign io_mem_aw_bits_len = wr_len; // @[VME.scala 238:22:@723.4]
  assign io_mem_w_valid = _T_228 & io_vme_wr_0_data_valid; // @[VME.scala 240:18:@726.4]
  assign io_mem_w_bits_data = io_vme_wr_0_data_bits; // @[VME.scala 241:22:@727.4]
  assign io_mem_w_bits_last = wr_cnt == io_vme_wr_0_cmd_bits_len; // @[VME.scala 242:22:@729.4]
  assign io_mem_b_ready = wstate == 2'h3; // @[VME.scala 244:18:@731.4]
  assign io_mem_ar_valid = rstate == 2'h1; // @[VME.scala 246:19:@733.4]
  assign io_mem_ar_bits_addr = rd_addr; // @[VME.scala 247:23:@734.4]
  assign io_mem_ar_bits_len = rd_len; // @[VME.scala 248:22:@735.4]
  assign io_mem_r_ready = _T_236 & io_vme_rd_0_data_ready; // @[VME.scala 250:18:@738.4]
  assign io_vme_rd_0_cmd_ready = rd_arb_io_in_0_ready; // @[VME.scala 147:53:@626.4]
  assign io_vme_rd_0_data_valid = io_mem_r_valid; // @[VME.scala 227:29:@711.4]
  assign io_vme_rd_0_data_bits = io_mem_r_bits_data; // @[VME.scala 228:28:@712.4]
  assign io_vme_wr_0_cmd_ready = wstate == 2'h0; // @[VME.scala 231:26:@714.4]
  assign io_vme_wr_0_data_ready = _T_228 & io_mem_w_ready; // @[VME.scala 233:27:@719.4]
  assign io_vme_wr_0_ack = io_mem_b_ready & io_mem_b_valid; // @[VME.scala 232:20:@716.4]
  assign rd_arb_io_in_0_valid = io_vme_rd_0_cmd_valid; // @[VME.scala 147:53:@625.4]
  assign rd_arb_io_in_0_bits_addr = io_vme_rd_0_cmd_bits_addr; // @[VME.scala 147:53:@624.4]
  assign rd_arb_io_in_0_bits_len = io_vme_rd_0_cmd_bits_len; // @[VME.scala 147:53:@623.4]
  assign rd_arb_io_out_ready = rstate == 2'h0; // @[VME.scala 223:23:@708.4]
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
`ifdef RANDOMIZE
  integer initvar;
  initial begin
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      #0.002 begin end
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
  wr_cnt = _RAND_2[3:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  rd_len = _RAND_3[3:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  wr_len = _RAND_4[3:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  rd_addr = _RAND_5[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  wr_addr = _RAND_6[31:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge clock) begin
    if (reset) begin
      rstate <= 2'h0;
    end else begin
      if (_T_191) begin
        if (rd_arb_io_out_valid) begin
          rstate <= 2'h1;
        end
      end else begin
        if (_T_192) begin
          if (io_mem_ar_ready) begin
            rstate <= 2'h2;
          end
        end else begin
          if (_T_193) begin
            if (_T_195) begin
              rstate <= 2'h0;
            end
          end
        end
      end
    end
    if (reset) begin
      wstate <= 2'h0;
    end else begin
      if (_T_205) begin
        if (io_vme_wr_0_cmd_valid) begin
          wstate <= 2'h1;
        end
      end else begin
        if (_T_206) begin
          if (io_mem_aw_ready) begin
            wstate <= 2'h2;
          end
        end else begin
          if (_T_207) begin
            if (_T_210) begin
              wstate <= 2'h3;
            end
          end else begin
            if (_T_211) begin
              if (io_mem_b_valid) begin
                wstate <= 2'h0;
              end
            end
          end
        end
      end
    end
    if (reset) begin
      wr_cnt <= 4'h0;
    end else begin
      if (_T_199) begin
        wr_cnt <= 4'h0;
      end else begin
        if (_T_201) begin
          wr_cnt <= _T_204;
        end
      end
    end
    if (reset) begin
      rd_len <= 4'h0;
    end else begin
      if (_T_188) begin
        rd_len <= rd_arb_io_out_bits_len;
      end
    end
    if (reset) begin
      wr_len <= 4'h0;
    end else begin
      if (_T_221) begin
        wr_len <= io_vme_wr_0_cmd_bits_len;
      end
    end
    if (reset) begin
      rd_addr <= 32'h0;
    end else begin
      if (_T_188) begin
        rd_addr <= rd_arb_io_out_bits_addr;
      end
    end
    if (reset) begin
      wr_addr <= 32'h0;
    end else begin
      if (_T_221) begin
        wr_addr <= io_vme_wr_0_cmd_bits_addr;
      end
    end
  end
endmodule
module Queue( // @[:@762.2]
  input         clock, // @[:@763.4]
  input         reset, // @[:@764.4]
  output        io_enq_ready, // @[:@765.4]
  input         io_enq_valid, // @[:@765.4]
  input  [63:0] io_enq_bits, // @[:@765.4]
  input         io_deq_ready, // @[:@765.4]
  output        io_deq_valid, // @[:@765.4]
  output [63:0] io_deq_bits // @[:@765.4]
);
  reg [63:0] ram [0:39]; // @[Decoupled.scala 215:24:@767.4]
  reg [63:0] _RAND_0;
  wire [63:0] ram__T_65_data; // @[Decoupled.scala 215:24:@767.4]
  wire [5:0] ram__T_65_addr; // @[Decoupled.scala 215:24:@767.4]
  reg [63:0] _RAND_1;
  wire [63:0] ram__T_49_data; // @[Decoupled.scala 215:24:@767.4]
  wire [5:0] ram__T_49_addr; // @[Decoupled.scala 215:24:@767.4]
  wire  ram__T_49_mask; // @[Decoupled.scala 215:24:@767.4]
  wire  ram__T_49_en; // @[Decoupled.scala 215:24:@767.4]
  reg [5:0] value; // @[Counter.scala 26:33:@768.4]
  reg [31:0] _RAND_2;
  reg [5:0] value_1; // @[Counter.scala 26:33:@769.4]
  reg [31:0] _RAND_3;
  reg  maybe_full; // @[Decoupled.scala 218:35:@770.4]
  reg [31:0] _RAND_4;
  wire  _T_41; // @[Decoupled.scala 220:41:@771.4]
  wire  _T_43; // @[Decoupled.scala 221:36:@772.4]
  wire  empty; // @[Decoupled.scala 221:33:@773.4]
  wire  _T_44; // @[Decoupled.scala 222:32:@774.4]
  wire  do_enq; // @[Decoupled.scala 37:37:@775.4]
  wire  do_deq; // @[Decoupled.scala 37:37:@778.4]
  wire  wrap; // @[Counter.scala 34:24:@784.6]
  wire [6:0] _T_52; // @[Counter.scala 35:22:@785.6]
  wire [5:0] _T_53; // @[Counter.scala 35:22:@786.6]
  wire [5:0] _GEN_0; // @[Counter.scala 37:21:@788.6]
  wire [5:0] _GEN_6; // @[Decoupled.scala 226:17:@781.4]
  wire  wrap_1; // @[Counter.scala 34:24:@793.6]
  wire [6:0] _T_57; // @[Counter.scala 35:22:@794.6]
  wire [5:0] _T_58; // @[Counter.scala 35:22:@795.6]
  wire [5:0] _GEN_7; // @[Counter.scala 37:21:@797.6]
  wire [5:0] _GEN_8; // @[Decoupled.scala 230:17:@792.4]
  wire  _T_60; // @[Decoupled.scala 233:16:@801.4]
  wire  _GEN_9; // @[Decoupled.scala 233:28:@802.4]
  assign ram__T_65_addr = value_1;
  `ifndef RANDOMIZE_GARBAGE_ASSIGN
  assign ram__T_65_data = ram[ram__T_65_addr]; // @[Decoupled.scala 215:24:@767.4]
  `else
  assign ram__T_65_data = ram__T_65_addr >= 6'h28 ? _RAND_1[63:0] : ram[ram__T_65_addr]; // @[Decoupled.scala 215:24:@767.4]
  `endif // RANDOMIZE_GARBAGE_ASSIGN
  assign ram__T_49_data = io_enq_bits;
  assign ram__T_49_addr = value;
  assign ram__T_49_mask = 1'h1;
  assign ram__T_49_en = io_enq_ready & io_enq_valid;
  assign _T_41 = value == value_1; // @[Decoupled.scala 220:41:@771.4]
  assign _T_43 = maybe_full == 1'h0; // @[Decoupled.scala 221:36:@772.4]
  assign empty = _T_41 & _T_43; // @[Decoupled.scala 221:33:@773.4]
  assign _T_44 = _T_41 & maybe_full; // @[Decoupled.scala 222:32:@774.4]
  assign do_enq = io_enq_ready & io_enq_valid; // @[Decoupled.scala 37:37:@775.4]
  assign do_deq = io_deq_ready & io_deq_valid; // @[Decoupled.scala 37:37:@778.4]
  assign wrap = value == 6'h27; // @[Counter.scala 34:24:@784.6]
  assign _T_52 = value + 6'h1; // @[Counter.scala 35:22:@785.6]
  assign _T_53 = value + 6'h1; // @[Counter.scala 35:22:@786.6]
  assign _GEN_0 = wrap ? 6'h0 : _T_53; // @[Counter.scala 37:21:@788.6]
  assign _GEN_6 = do_enq ? _GEN_0 : value; // @[Decoupled.scala 226:17:@781.4]
  assign wrap_1 = value_1 == 6'h27; // @[Counter.scala 34:24:@793.6]
  assign _T_57 = value_1 + 6'h1; // @[Counter.scala 35:22:@794.6]
  assign _T_58 = value_1 + 6'h1; // @[Counter.scala 35:22:@795.6]
  assign _GEN_7 = wrap_1 ? 6'h0 : _T_58; // @[Counter.scala 37:21:@797.6]
  assign _GEN_8 = do_deq ? _GEN_7 : value_1; // @[Decoupled.scala 230:17:@792.4]
  assign _T_60 = do_enq != do_deq; // @[Decoupled.scala 233:16:@801.4]
  assign _GEN_9 = _T_60 ? do_enq : maybe_full; // @[Decoupled.scala 233:28:@802.4]
  assign io_enq_ready = _T_44 == 1'h0; // @[Decoupled.scala 238:16:@808.4]
  assign io_deq_valid = empty == 1'h0; // @[Decoupled.scala 237:16:@806.4]
  assign io_deq_bits = ram__T_65_data; // @[Decoupled.scala 239:15:@810.4]
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
`ifdef RANDOMIZE
  integer initvar;
  initial begin
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      #0.002 begin end
    `endif
  _RAND_0 = {2{`RANDOM}};
  `ifdef RANDOMIZE_MEM_INIT
  for (initvar = 0; initvar < 40; initvar = initvar+1)
    ram[initvar] = _RAND_0[63:0];
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
  maybe_full = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  end
`endif // RANDOMIZE
  always @(posedge clock) begin
    if(ram__T_49_en & ram__T_49_mask) begin
      ram[ram__T_49_addr] <= ram__T_49_data; // @[Decoupled.scala 215:24:@767.4]
    end
    if (reset) begin
      value <= 6'h0;
    end else begin
      if (do_enq) begin
        if (wrap) begin
          value <= 6'h0;
        end else begin
          value <= _T_53;
        end
      end
    end
    if (reset) begin
      value_1 <= 6'h0;
    end else begin
      if (do_deq) begin
        if (wrap_1) begin
          value_1 <= 6'h0;
        end else begin
          value_1 <= _T_58;
        end
      end
    end
    if (reset) begin
      maybe_full <= 1'h0;
    end else begin
      if (_T_60) begin
        maybe_full <= do_enq;
      end
    end
  end
endmodule
module VTAShell( // @[:@822.2]
  input         clock, // @[:@823.4]
  input         reset, // @[:@824.4]
  output        io_host_aw_ready, // @[:@825.4]
  input         io_host_aw_valid, // @[:@825.4]
  input  [15:0] io_host_aw_bits_addr, // @[:@825.4]
  output        io_host_w_ready, // @[:@825.4]
  input         io_host_w_valid, // @[:@825.4]
  input  [31:0] io_host_w_bits_data, // @[:@825.4]
  input         io_host_b_ready, // @[:@825.4]
  output        io_host_b_valid, // @[:@825.4]
  output        io_host_ar_ready, // @[:@825.4]
  input         io_host_ar_valid, // @[:@825.4]
  input  [15:0] io_host_ar_bits_addr, // @[:@825.4]
  input         io_host_r_ready, // @[:@825.4]
  output        io_host_r_valid, // @[:@825.4]
  output [31:0] io_host_r_bits_data, // @[:@825.4]
  input         io_mem_aw_ready, // @[:@825.4]
  output        io_mem_aw_valid, // @[:@825.4]
  output [31:0] io_mem_aw_bits_addr, // @[:@825.4]
  output [3:0]  io_mem_aw_bits_len, // @[:@825.4]
  input         io_mem_w_ready, // @[:@825.4]
  output        io_mem_w_valid, // @[:@825.4]
  output [63:0] io_mem_w_bits_data, // @[:@825.4]
  output        io_mem_w_bits_last, // @[:@825.4]
  output        io_mem_b_ready, // @[:@825.4]
  input         io_mem_b_valid, // @[:@825.4]
  input         io_mem_ar_ready, // @[:@825.4]
  output        io_mem_ar_valid, // @[:@825.4]
  output [31:0] io_mem_ar_bits_addr, // @[:@825.4]
  output [3:0]  io_mem_ar_bits_len, // @[:@825.4]
  output        io_mem_r_ready, // @[:@825.4]
  input         io_mem_r_valid, // @[:@825.4]
  input  [63:0] io_mem_r_bits_data, // @[:@825.4]
  input         io_mem_r_bits_last // @[:@825.4]
);
  wire  vcr_clock; // @[VTAShell.scala 85:19:@827.4]
  wire  vcr_reset; // @[VTAShell.scala 85:19:@827.4]
  wire  vcr_io_host_aw_ready; // @[VTAShell.scala 85:19:@827.4]
  wire  vcr_io_host_aw_valid; // @[VTAShell.scala 85:19:@827.4]
  wire [15:0] vcr_io_host_aw_bits_addr; // @[VTAShell.scala 85:19:@827.4]
  wire  vcr_io_host_w_ready; // @[VTAShell.scala 85:19:@827.4]
  wire  vcr_io_host_w_valid; // @[VTAShell.scala 85:19:@827.4]
  wire [31:0] vcr_io_host_w_bits_data; // @[VTAShell.scala 85:19:@827.4]
  wire  vcr_io_host_b_ready; // @[VTAShell.scala 85:19:@827.4]
  wire  vcr_io_host_b_valid; // @[VTAShell.scala 85:19:@827.4]
  wire  vcr_io_host_ar_ready; // @[VTAShell.scala 85:19:@827.4]
  wire  vcr_io_host_ar_valid; // @[VTAShell.scala 85:19:@827.4]
  wire [15:0] vcr_io_host_ar_bits_addr; // @[VTAShell.scala 85:19:@827.4]
  wire  vcr_io_host_r_ready; // @[VTAShell.scala 85:19:@827.4]
  wire  vcr_io_host_r_valid; // @[VTAShell.scala 85:19:@827.4]
  wire [31:0] vcr_io_host_r_bits_data; // @[VTAShell.scala 85:19:@827.4]
  wire  vcr_io_vcr_launch; // @[VTAShell.scala 85:19:@827.4]
  wire  vcr_io_vcr_finish; // @[VTAShell.scala 85:19:@827.4]
  wire  vcr_io_vcr_ecnt_0_valid; // @[VTAShell.scala 85:19:@827.4]
  wire [31:0] vcr_io_vcr_ecnt_0_bits; // @[VTAShell.scala 85:19:@827.4]
  wire [31:0] vcr_io_vcr_vals_0; // @[VTAShell.scala 85:19:@827.4]
  wire [31:0] vcr_io_vcr_vals_1; // @[VTAShell.scala 85:19:@827.4]
  wire [31:0] vcr_io_vcr_ptrs_0; // @[VTAShell.scala 85:19:@827.4]
  wire [31:0] vcr_io_vcr_ptrs_2; // @[VTAShell.scala 85:19:@827.4]
  wire  vmem_clock; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_reset; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_mem_aw_ready; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_mem_aw_valid; // @[VTAShell.scala 86:20:@830.4]
  wire [31:0] vmem_io_mem_aw_bits_addr; // @[VTAShell.scala 86:20:@830.4]
  wire [3:0] vmem_io_mem_aw_bits_len; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_mem_w_ready; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_mem_w_valid; // @[VTAShell.scala 86:20:@830.4]
  wire [63:0] vmem_io_mem_w_bits_data; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_mem_w_bits_last; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_mem_b_ready; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_mem_b_valid; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_mem_ar_ready; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_mem_ar_valid; // @[VTAShell.scala 86:20:@830.4]
  wire [31:0] vmem_io_mem_ar_bits_addr; // @[VTAShell.scala 86:20:@830.4]
  wire [3:0] vmem_io_mem_ar_bits_len; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_mem_r_ready; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_mem_r_valid; // @[VTAShell.scala 86:20:@830.4]
  wire [63:0] vmem_io_mem_r_bits_data; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_mem_r_bits_last; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_vme_rd_0_cmd_ready; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_vme_rd_0_cmd_valid; // @[VTAShell.scala 86:20:@830.4]
  wire [31:0] vmem_io_vme_rd_0_cmd_bits_addr; // @[VTAShell.scala 86:20:@830.4]
  wire [3:0] vmem_io_vme_rd_0_cmd_bits_len; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_vme_rd_0_data_ready; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_vme_rd_0_data_valid; // @[VTAShell.scala 86:20:@830.4]
  wire [63:0] vmem_io_vme_rd_0_data_bits; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_vme_wr_0_cmd_ready; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_vme_wr_0_cmd_valid; // @[VTAShell.scala 86:20:@830.4]
  wire [31:0] vmem_io_vme_wr_0_cmd_bits_addr; // @[VTAShell.scala 86:20:@830.4]
  wire [3:0] vmem_io_vme_wr_0_cmd_bits_len; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_vme_wr_0_data_ready; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_vme_wr_0_data_valid; // @[VTAShell.scala 86:20:@830.4]
  wire [63:0] vmem_io_vme_wr_0_data_bits; // @[VTAShell.scala 86:20:@830.4]
  wire  vmem_io_vme_wr_0_ack; // @[VTAShell.scala 86:20:@830.4]
  wire  buffer_clock; // @[VTAShell.scala 87:22:@833.4]
  wire  buffer_reset; // @[VTAShell.scala 87:22:@833.4]
  wire  buffer_io_enq_ready; // @[VTAShell.scala 87:22:@833.4]
  wire  buffer_io_enq_valid; // @[VTAShell.scala 87:22:@833.4]
  wire [63:0] buffer_io_enq_bits; // @[VTAShell.scala 87:22:@833.4]
  wire  buffer_io_deq_ready; // @[VTAShell.scala 87:22:@833.4]
  wire  buffer_io_deq_valid; // @[VTAShell.scala 87:22:@833.4]
  wire [63:0] buffer_io_deq_bits; // @[VTAShell.scala 87:22:@833.4]
  reg [1:0] Rstate; // @[VTAShell.scala 90:23:@836.4]
  reg [31:0] _RAND_0;
  reg [1:0] Wstate; // @[VTAShell.scala 91:23:@837.4]
  reg [31:0] _RAND_1;
  reg [7:0] value; // @[Counter.scala 26:33:@838.4]
  reg [31:0] _RAND_2;
  wire  _T_99; // @[VTAShell.scala 95:16:@839.4]
  wire  _T_101; // @[Counter.scala 34:24:@841.6]
  wire [8:0] _T_103; // @[Counter.scala 35:22:@842.6]
  wire [7:0] _T_104; // @[Counter.scala 35:22:@843.6]
  wire [7:0] _GEN_0; // @[Counter.scala 37:21:@845.6]
  wire [7:0] _GEN_1; // @[VTAShell.scala 95:27:@840.4]
  wire  _T_109; // @[Conditional.scala 37:30:@850.4]
  wire [7:0] _GEN_2; // @[VTAShell.scala 105:32:@852.6]
  wire [1:0] _GEN_3; // @[VTAShell.scala 105:32:@852.6]
  wire  _T_111; // @[Conditional.scala 37:30:@858.6]
  wire  _T_112; // @[Decoupled.scala 37:37:@860.8]
  wire [1:0] _GEN_4; // @[VTAShell.scala 111:43:@861.8]
  wire [1:0] _GEN_5; // @[Conditional.scala 39:67:@859.6]
  wire [7:0] _GEN_6; // @[Conditional.scala 40:58:@851.4]
  wire [1:0] _GEN_7; // @[Conditional.scala 40:58:@851.4]
  wire  _T_113; // @[Conditional.scala 37:30:@865.4]
  wire [1:0] _GEN_8; // @[VTAShell.scala 119:32:@867.6]
  wire  _T_114; // @[Conditional.scala 37:30:@872.6]
  wire  _T_115; // @[Decoupled.scala 37:37:@874.8]
  wire [1:0] _GEN_9; // @[VTAShell.scala 124:43:@875.8]
  wire [1:0] _GEN_10; // @[Conditional.scala 39:67:@873.6]
  wire [1:0] _GEN_11; // @[Conditional.scala 40:58:@866.4]
  wire  _T_122; // @[VTAShell.scala 147:21:@893.4]
  wire [1:0] _GEN_14; // @[VTAShell.scala 151:31:@897.4]
  wire [1:0] _GEN_15; // @[VTAShell.scala 151:31:@897.4]
  wire [63:0] _GEN_16; // @[VTAShell.scala 158:53:@904.4]
  wire [64:0] _T_123; // @[VTAShell.scala 158:53:@904.4]
  VCR vcr ( // @[VTAShell.scala 85:19:@827.4]
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
  VME vmem ( // @[VTAShell.scala 86:20:@830.4]
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
  Queue buffer ( // @[VTAShell.scala 87:22:@833.4]
    .clock(buffer_clock),
    .reset(buffer_reset),
    .io_enq_ready(buffer_io_enq_ready),
    .io_enq_valid(buffer_io_enq_valid),
    .io_enq_bits(buffer_io_enq_bits),
    .io_deq_ready(buffer_io_deq_ready),
    .io_deq_valid(buffer_io_deq_valid),
    .io_deq_bits(buffer_io_deq_bits)
  );
  assign _T_99 = Rstate != 2'h0; // @[VTAShell.scala 95:16:@839.4]
  assign _T_101 = value == 8'hc7; // @[Counter.scala 34:24:@841.6]
  assign _T_103 = value + 8'h1; // @[Counter.scala 35:22:@842.6]
  assign _T_104 = value + 8'h1; // @[Counter.scala 35:22:@843.6]
  assign _GEN_0 = _T_101 ? 8'h0 : _T_104; // @[Counter.scala 37:21:@845.6]
  assign _GEN_1 = _T_99 ? _GEN_0 : value; // @[VTAShell.scala 95:27:@840.4]
  assign _T_109 = 2'h0 == Rstate; // @[Conditional.scala 37:30:@850.4]
  assign _GEN_2 = vcr_io_vcr_launch ? 8'h0 : _GEN_1; // @[VTAShell.scala 105:32:@852.6]
  assign _GEN_3 = vcr_io_vcr_launch ? 2'h1 : Rstate; // @[VTAShell.scala 105:32:@852.6]
  assign _T_111 = 2'h1 == Rstate; // @[Conditional.scala 37:30:@858.6]
  assign _T_112 = vmem_io_vme_rd_0_cmd_ready & vmem_io_vme_rd_0_cmd_valid; // @[Decoupled.scala 37:37:@860.8]
  assign _GEN_4 = _T_112 ? 2'h2 : Rstate; // @[VTAShell.scala 111:43:@861.8]
  assign _GEN_5 = _T_111 ? _GEN_4 : Rstate; // @[Conditional.scala 39:67:@859.6]
  assign _GEN_6 = _T_109 ? _GEN_2 : _GEN_1; // @[Conditional.scala 40:58:@851.4]
  assign _GEN_7 = _T_109 ? _GEN_3 : _GEN_5; // @[Conditional.scala 40:58:@851.4]
  assign _T_113 = 2'h0 == Wstate; // @[Conditional.scala 37:30:@865.4]
  assign _GEN_8 = vcr_io_vcr_launch ? 2'h1 : Wstate; // @[VTAShell.scala 119:32:@867.6]
  assign _T_114 = 2'h1 == Wstate; // @[Conditional.scala 37:30:@872.6]
  assign _T_115 = vmem_io_vme_wr_0_cmd_ready & vmem_io_vme_wr_0_cmd_valid; // @[Decoupled.scala 37:37:@874.8]
  assign _GEN_9 = _T_115 ? 2'h2 : Wstate; // @[VTAShell.scala 124:43:@875.8]
  assign _GEN_10 = _T_114 ? _GEN_9 : Wstate; // @[Conditional.scala 39:67:@873.6]
  assign _GEN_11 = _T_113 ? _GEN_8 : _GEN_10; // @[Conditional.scala 40:58:@866.4]
  assign _T_122 = Wstate == 2'h2; // @[VTAShell.scala 147:21:@893.4]
  assign _GEN_14 = vmem_io_vme_wr_0_ack ? 2'h0 : _GEN_7; // @[VTAShell.scala 151:31:@897.4]
  assign _GEN_15 = vmem_io_vme_wr_0_ack ? 2'h0 : _GEN_11; // @[VTAShell.scala 151:31:@897.4]
  assign _GEN_16 = {{32'd0}, vcr_io_vcr_vals_0}; // @[VTAShell.scala 158:53:@904.4]
  assign _T_123 = vmem_io_vme_rd_0_data_bits + _GEN_16; // @[VTAShell.scala 158:53:@904.4]
  assign io_host_aw_ready = vcr_io_host_aw_ready; // @[VTAShell.scala 162:15:@971.4]
  assign io_host_w_ready = vcr_io_host_w_ready; // @[VTAShell.scala 162:15:@968.4]
  assign io_host_b_valid = vcr_io_host_b_valid; // @[VTAShell.scala 162:15:@963.4]
  assign io_host_ar_ready = vcr_io_host_ar_ready; // @[VTAShell.scala 162:15:@961.4]
  assign io_host_r_valid = vcr_io_host_r_valid; // @[VTAShell.scala 162:15:@957.4]
  assign io_host_r_bits_data = vcr_io_host_r_bits_data; // @[VTAShell.scala 162:15:@956.4]
  assign io_mem_aw_valid = vmem_io_mem_aw_valid; // @[VTAShell.scala 161:10:@953.4]
  assign io_mem_aw_bits_addr = vmem_io_mem_aw_bits_addr; // @[VTAShell.scala 161:10:@952.4]
  assign io_mem_aw_bits_len = vmem_io_mem_aw_bits_len; // @[VTAShell.scala 161:10:@949.4]
  assign io_mem_w_valid = vmem_io_mem_w_valid; // @[VTAShell.scala 161:10:@940.4]
  assign io_mem_w_bits_data = vmem_io_mem_w_bits_data; // @[VTAShell.scala 161:10:@939.4]
  assign io_mem_w_bits_last = vmem_io_mem_w_bits_last; // @[VTAShell.scala 161:10:@937.4]
  assign io_mem_b_ready = vmem_io_mem_b_ready; // @[VTAShell.scala 161:10:@934.4]
  assign io_mem_ar_valid = vmem_io_mem_ar_valid; // @[VTAShell.scala 161:10:@928.4]
  assign io_mem_ar_bits_addr = vmem_io_mem_ar_bits_addr; // @[VTAShell.scala 161:10:@927.4]
  assign io_mem_ar_bits_len = vmem_io_mem_ar_bits_len; // @[VTAShell.scala 161:10:@924.4]
  assign io_mem_r_ready = vmem_io_mem_r_ready; // @[VTAShell.scala 161:10:@916.4]
  assign vcr_clock = clock; // @[:@828.4]
  assign vcr_reset = reset; // @[:@829.4]
  assign vcr_io_host_aw_valid = io_host_aw_valid; // @[VTAShell.scala 162:15:@970.4]
  assign vcr_io_host_aw_bits_addr = io_host_aw_bits_addr; // @[VTAShell.scala 162:15:@969.4]
  assign vcr_io_host_w_valid = io_host_w_valid; // @[VTAShell.scala 162:15:@967.4]
  assign vcr_io_host_w_bits_data = io_host_w_bits_data; // @[VTAShell.scala 162:15:@966.4]
  assign vcr_io_host_b_ready = io_host_b_ready; // @[VTAShell.scala 162:15:@964.4]
  assign vcr_io_host_ar_valid = io_host_ar_valid; // @[VTAShell.scala 162:15:@960.4]
  assign vcr_io_host_ar_bits_addr = io_host_ar_bits_addr; // @[VTAShell.scala 162:15:@959.4]
  assign vcr_io_host_r_ready = io_host_r_ready; // @[VTAShell.scala 162:15:@958.4]
  assign vcr_io_vcr_finish = _T_122 & vmem_io_vme_wr_0_ack; // @[VTAShell.scala 148:21:@895.4]
  assign vcr_io_vcr_ecnt_0_valid = _T_122 & vmem_io_vme_wr_0_ack; // @[VTAShell.scala 149:28:@896.4]
  assign vcr_io_vcr_ecnt_0_bits = {{24'd0}, value}; // @[VTAShell.scala 100:29:@849.4]
  assign vmem_clock = clock; // @[:@831.4]
  assign vmem_reset = reset; // @[:@832.4]
  assign vmem_io_mem_aw_ready = io_mem_aw_ready; // @[VTAShell.scala 161:10:@954.4]
  assign vmem_io_mem_w_ready = io_mem_w_ready; // @[VTAShell.scala 161:10:@941.4]
  assign vmem_io_mem_b_valid = io_mem_b_valid; // @[VTAShell.scala 161:10:@933.4]
  assign vmem_io_mem_ar_ready = io_mem_ar_ready; // @[VTAShell.scala 161:10:@929.4]
  assign vmem_io_mem_r_valid = io_mem_r_valid; // @[VTAShell.scala 161:10:@915.4]
  assign vmem_io_mem_r_bits_data = io_mem_r_bits_data; // @[VTAShell.scala 161:10:@914.4]
  assign vmem_io_mem_r_bits_last = io_mem_r_bits_last; // @[VTAShell.scala 161:10:@912.4]
  assign vmem_io_vme_rd_0_cmd_valid = Rstate == 2'h1; // @[VTAShell.scala 132:31:@881.4 VTAShell.scala 139:33:@887.6]
  assign vmem_io_vme_rd_0_cmd_bits_addr = vcr_io_vcr_ptrs_0; // @[VTAShell.scala 130:35:@879.4]
  assign vmem_io_vme_rd_0_cmd_bits_len = vcr_io_vcr_vals_1[3:0]; // @[VTAShell.scala 131:34:@880.4]
  assign vmem_io_vme_rd_0_data_ready = buffer_io_enq_ready; // @[VTAShell.scala 157:17:@903.4]
  assign vmem_io_vme_wr_0_cmd_valid = Wstate == 2'h1; // @[VTAShell.scala 136:31:@884.4 VTAShell.scala 143:33:@891.6]
  assign vmem_io_vme_wr_0_cmd_bits_addr = vcr_io_vcr_ptrs_2; // @[VTAShell.scala 134:35:@882.4]
  assign vmem_io_vme_wr_0_cmd_bits_len = vcr_io_vcr_vals_1[3:0]; // @[VTAShell.scala 135:34:@883.4]
  assign vmem_io_vme_wr_0_data_valid = buffer_io_deq_valid; // @[VTAShell.scala 159:26:@908.4]
  assign vmem_io_vme_wr_0_data_bits = buffer_io_deq_bits; // @[VTAShell.scala 159:26:@907.4]
  assign buffer_clock = clock; // @[:@834.4]
  assign buffer_reset = reset; // @[:@835.4]
  assign buffer_io_enq_valid = vmem_io_vme_rd_0_data_valid; // @[VTAShell.scala 157:17:@902.4]
  assign buffer_io_enq_bits = vmem_io_vme_rd_0_data_bits + _GEN_16; // @[VTAShell.scala 157:17:@901.4 VTAShell.scala 158:22:@906.4]
  assign buffer_io_deq_ready = vmem_io_vme_wr_0_data_ready; // @[VTAShell.scala 159:26:@909.4]
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
`ifdef RANDOMIZE
  integer initvar;
  initial begin
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      #0.002 begin end
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
  end
`endif // RANDOMIZE
  always @(posedge clock) begin
    if (reset) begin
      Rstate <= 2'h0;
    end else begin
      if (vmem_io_vme_wr_0_ack) begin
        Rstate <= 2'h0;
      end else begin
        if (_T_109) begin
          if (vcr_io_vcr_launch) begin
            Rstate <= 2'h1;
          end
        end else begin
          if (_T_111) begin
            if (_T_112) begin
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
        if (_T_113) begin
          if (vcr_io_vcr_launch) begin
            Wstate <= 2'h1;
          end
        end else begin
          if (_T_114) begin
            if (_T_115) begin
              Wstate <= 2'h2;
            end
          end
        end
      end
    end
    if (reset) begin
      value <= 8'h0;
    end else begin
      if (_T_109) begin
        if (vcr_io_vcr_launch) begin
          value <= 8'h0;
        end else begin
          if (_T_99) begin
            if (_T_101) begin
              value <= 8'h0;
            end else begin
              value <= _T_104;
            end
          end
        end
      end else begin
        if (_T_99) begin
          if (_T_101) begin
            value <= 8'h0;
          end else begin
            value <= _T_104;
          end
        end
      end
    end
  end
endmodule
module TestAccel2( // @[:@973.2]
  input   clock, // @[:@974.4]
  input   reset, // @[:@975.4]
  input   sim_clock, // @[:@976.4]
  output  sim_wait // @[:@977.4]
);
  wire  sim_shell_clock; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_reset; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_mem_aw_ready; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_mem_aw_valid; // @[VCRAccel.scala 54:25:@979.4]
  wire [31:0] sim_shell_mem_aw_bits_addr; // @[VCRAccel.scala 54:25:@979.4]
  wire [3:0] sim_shell_mem_aw_bits_len; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_mem_w_ready; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_mem_w_valid; // @[VCRAccel.scala 54:25:@979.4]
  wire [63:0] sim_shell_mem_w_bits_data; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_mem_w_bits_last; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_mem_b_ready; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_mem_b_valid; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_mem_ar_ready; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_mem_ar_valid; // @[VCRAccel.scala 54:25:@979.4]
  wire [31:0] sim_shell_mem_ar_bits_addr; // @[VCRAccel.scala 54:25:@979.4]
  wire [3:0] sim_shell_mem_ar_bits_len; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_mem_r_ready; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_mem_r_valid; // @[VCRAccel.scala 54:25:@979.4]
  wire [63:0] sim_shell_mem_r_bits_data; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_mem_r_bits_last; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_host_aw_ready; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_host_aw_valid; // @[VCRAccel.scala 54:25:@979.4]
  wire [15:0] sim_shell_host_aw_bits_addr; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_host_w_ready; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_host_w_valid; // @[VCRAccel.scala 54:25:@979.4]
  wire [31:0] sim_shell_host_w_bits_data; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_host_b_ready; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_host_b_valid; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_host_ar_ready; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_host_ar_valid; // @[VCRAccel.scala 54:25:@979.4]
  wire [15:0] sim_shell_host_ar_bits_addr; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_host_r_ready; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_host_r_valid; // @[VCRAccel.scala 54:25:@979.4]
  wire [31:0] sim_shell_host_r_bits_data; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_sim_clock; // @[VCRAccel.scala 54:25:@979.4]
  wire  sim_shell_sim_wait; // @[VCRAccel.scala 54:25:@979.4]
  wire  vta_shell_clock; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_reset; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_host_aw_ready; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_host_aw_valid; // @[VCRAccel.scala 55:25:@982.4]
  wire [15:0] vta_shell_io_host_aw_bits_addr; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_host_w_ready; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_host_w_valid; // @[VCRAccel.scala 55:25:@982.4]
  wire [31:0] vta_shell_io_host_w_bits_data; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_host_b_ready; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_host_b_valid; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_host_ar_ready; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_host_ar_valid; // @[VCRAccel.scala 55:25:@982.4]
  wire [15:0] vta_shell_io_host_ar_bits_addr; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_host_r_ready; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_host_r_valid; // @[VCRAccel.scala 55:25:@982.4]
  wire [31:0] vta_shell_io_host_r_bits_data; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_mem_aw_ready; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_mem_aw_valid; // @[VCRAccel.scala 55:25:@982.4]
  wire [31:0] vta_shell_io_mem_aw_bits_addr; // @[VCRAccel.scala 55:25:@982.4]
  wire [3:0] vta_shell_io_mem_aw_bits_len; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_mem_w_ready; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_mem_w_valid; // @[VCRAccel.scala 55:25:@982.4]
  wire [63:0] vta_shell_io_mem_w_bits_data; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_mem_w_bits_last; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_mem_b_ready; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_mem_b_valid; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_mem_ar_ready; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_mem_ar_valid; // @[VCRAccel.scala 55:25:@982.4]
  wire [31:0] vta_shell_io_mem_ar_bits_addr; // @[VCRAccel.scala 55:25:@982.4]
  wire [3:0] vta_shell_io_mem_ar_bits_len; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_mem_r_ready; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_mem_r_valid; // @[VCRAccel.scala 55:25:@982.4]
  wire [63:0] vta_shell_io_mem_r_bits_data; // @[VCRAccel.scala 55:25:@982.4]
  wire  vta_shell_io_mem_r_bits_last; // @[VCRAccel.scala 55:25:@982.4]
  AXISimShell sim_shell ( // @[VCRAccel.scala 54:25:@979.4]
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
  VTAShell vta_shell ( // @[VCRAccel.scala 55:25:@982.4]
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
  assign sim_wait = sim_shell_sim_wait; // @[VCRAccel.scala 59:12:@1048.4]
  assign sim_shell_clock = clock; // @[:@980.4]
  assign sim_shell_reset = reset; // @[:@981.4]
  assign sim_shell_mem_aw_valid = vta_shell_io_mem_aw_valid; // @[VCRAccel.scala 60:17:@1092.4]
  assign sim_shell_mem_aw_bits_addr = vta_shell_io_mem_aw_bits_addr; // @[VCRAccel.scala 60:17:@1091.4]
  assign sim_shell_mem_aw_bits_len = vta_shell_io_mem_aw_bits_len; // @[VCRAccel.scala 60:17:@1088.4]
  assign sim_shell_mem_w_valid = vta_shell_io_mem_w_valid; // @[VCRAccel.scala 60:17:@1079.4]
  assign sim_shell_mem_w_bits_data = vta_shell_io_mem_w_bits_data; // @[VCRAccel.scala 60:17:@1078.4]
  assign sim_shell_mem_w_bits_last = vta_shell_io_mem_w_bits_last; // @[VCRAccel.scala 60:17:@1076.4]
  assign sim_shell_mem_b_ready = vta_shell_io_mem_b_ready; // @[VCRAccel.scala 60:17:@1073.4]
  assign sim_shell_mem_ar_valid = vta_shell_io_mem_ar_valid; // @[VCRAccel.scala 60:17:@1067.4]
  assign sim_shell_mem_ar_bits_addr = vta_shell_io_mem_ar_bits_addr; // @[VCRAccel.scala 60:17:@1066.4]
  assign sim_shell_mem_ar_bits_len = vta_shell_io_mem_ar_bits_len; // @[VCRAccel.scala 60:17:@1063.4]
  assign sim_shell_mem_r_ready = vta_shell_io_mem_r_ready; // @[VCRAccel.scala 60:17:@1055.4]
  assign sim_shell_host_aw_ready = vta_shell_io_host_aw_ready; // @[VCRAccel.scala 61:20:@1110.4]
  assign sim_shell_host_w_ready = vta_shell_io_host_w_ready; // @[VCRAccel.scala 61:20:@1107.4]
  assign sim_shell_host_b_valid = vta_shell_io_host_b_valid; // @[VCRAccel.scala 61:20:@1102.4]
  assign sim_shell_host_ar_ready = vta_shell_io_host_ar_ready; // @[VCRAccel.scala 61:20:@1100.4]
  assign sim_shell_host_r_valid = vta_shell_io_host_r_valid; // @[VCRAccel.scala 61:20:@1096.4]
  assign sim_shell_host_r_bits_data = vta_shell_io_host_r_bits_data; // @[VCRAccel.scala 61:20:@1095.4]
  assign sim_shell_sim_clock = sim_clock; // @[VCRAccel.scala 58:23:@1047.4]
  assign vta_shell_clock = clock; // @[:@983.4]
  assign vta_shell_reset = reset; // @[:@984.4]
  assign vta_shell_io_host_aw_valid = sim_shell_host_aw_valid; // @[VCRAccel.scala 61:20:@1109.4]
  assign vta_shell_io_host_aw_bits_addr = sim_shell_host_aw_bits_addr; // @[VCRAccel.scala 61:20:@1108.4]
  assign vta_shell_io_host_w_valid = sim_shell_host_w_valid; // @[VCRAccel.scala 61:20:@1106.4]
  assign vta_shell_io_host_w_bits_data = sim_shell_host_w_bits_data; // @[VCRAccel.scala 61:20:@1105.4]
  assign vta_shell_io_host_b_ready = sim_shell_host_b_ready; // @[VCRAccel.scala 61:20:@1103.4]
  assign vta_shell_io_host_ar_valid = sim_shell_host_ar_valid; // @[VCRAccel.scala 61:20:@1099.4]
  assign vta_shell_io_host_ar_bits_addr = sim_shell_host_ar_bits_addr; // @[VCRAccel.scala 61:20:@1098.4]
  assign vta_shell_io_host_r_ready = sim_shell_host_r_ready; // @[VCRAccel.scala 61:20:@1097.4]
  assign vta_shell_io_mem_aw_ready = sim_shell_mem_aw_ready; // @[VCRAccel.scala 60:17:@1093.4]
  assign vta_shell_io_mem_w_ready = sim_shell_mem_w_ready; // @[VCRAccel.scala 60:17:@1080.4]
  assign vta_shell_io_mem_b_valid = sim_shell_mem_b_valid; // @[VCRAccel.scala 60:17:@1072.4]
  assign vta_shell_io_mem_ar_ready = sim_shell_mem_ar_ready; // @[VCRAccel.scala 60:17:@1068.4]
  assign vta_shell_io_mem_r_valid = sim_shell_mem_r_valid; // @[VCRAccel.scala 60:17:@1054.4]
  assign vta_shell_io_mem_r_bits_data = sim_shell_mem_r_bits_data; // @[VCRAccel.scala 60:17:@1053.4]
  assign vta_shell_io_mem_r_bits_last = sim_shell_mem_r_bits_last; // @[VCRAccel.scala 60:17:@1051.4]
endmodule
