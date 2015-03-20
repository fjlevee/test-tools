package com.fjl.test.tools.sip.call;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class SipDialogList extends AbstractTableModel {
  /**
   * 
   */
  private static final long serialVersionUID = -6705325348925237651L;

  protected Vector<SipDialog> sipDialogs;

  protected String type;

  public SipDialogList() {
    sipDialogs = new Vector<SipDialog>();
  }

  public void add(SipDialog sipClientCall) {
    sipDialogs.add(sipClientCall);
    int index = sipDialogs.size() - 1;
    fireTableRowsInserted(index, index);
  }

  public void remove(int index) {
    sipDialogs.remove(index);
    fireTableRowsDeleted(index, index);
  }

  public SipDialog get(int index) {
    return sipDialogs.get(index);
  }
  
  public SipDialog getSipDialog(String callID){
    for (int i = 0; i < sipDialogs.size(); i++) {
      if (sipDialogs.get(i).getCallId().equals(callID)) {
        return sipDialogs.get(i);
      }
    }
    return null;
  }

  public void remove(String callID) {
    int index = -1;
    for (int i = 0; i < sipDialogs.size(); i++) {
      if (sipDialogs.get(i).getCallId().equals(callID)) {
        sipDialogs.remove(i);
        index = i;
      }
    }
   if(index!=-1){
     fireTableRowsDeleted(index, index);
   }
  }
  
  
  
  public int size() {
    return sipDialogs.size();
  }

  public int getColumnCount() {
    return 4;
  }

  public int getRowCount() {
    return sipDialogs.size();
  }

  public Class getColumnClass(int columnIndex) {
    return String.class;

  }

  public String getColumnName(int columnIndex) {
    switch (columnIndex) {
    case 0:
      return "Call ID";
    case 1:
      return "Remote Name";
    case 2:
      return "Phone number";
    case 3:
      return "Service";
    default:
      return null;
    }
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    SipDialog sipCall = sipDialogs.get(rowIndex);
    switch (columnIndex) {
    case 0: // callID
      return sipCall.getCallId();
    case 1: // remote name
      return sipCall.getRemoteDisplayName();
    case 2: // number
      return sipCall.getRemoteNumber();
    case 3: // service
      return "Sip Phone";

    default:
      return null;
    }
  }

  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return false;
  }

  public void setValueAt(Object value, int rowIndex, int columnIndex) {
    SipDialog sipCall = (SipDialog) sipDialogs.get(rowIndex);
    /*
    switch (columnIndex) { 
       case 0 : 
         sipCall.set()
         break;
       case 1 :
         sipCall.set()
         break;
         
       case 2 :
         sipCall.set()
         break;
       default :
       break;
     }

    fireTableCellUpdated(rowIndex, columnIndex);
    */
  }

  public Vector getContents() {
    return sipDialogs;
  }


}