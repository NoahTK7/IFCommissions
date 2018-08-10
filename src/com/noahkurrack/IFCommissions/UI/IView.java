package com.noahkurrack.IFCommissions.UI;

import javax.swing.*;

public interface IView {
    JPanel panel = null;
    void attachListeners();
    JPanel getPanel();
}