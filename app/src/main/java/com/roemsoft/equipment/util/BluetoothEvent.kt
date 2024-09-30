package com.roemsoft.equipment.util

import com.dothantech.printer.IDzPrinter.PrintProgress
import com.dothantech.printer.IDzPrinter.PrinterState
import com.jeremyliao.liveeventbus.core.LiveEvent

class BluetoothStateEvent(val state: PrinterState) : LiveEvent

class BluetoothProgressEvent(val progress: PrintProgress, val addiInfo: Any) : LiveEvent