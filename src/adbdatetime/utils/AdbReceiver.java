package adbdatetime.utils;

import com.android.ddmlib.MultiLineReceiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdbReceiver extends MultiLineReceiver {
    private List<String> adbOutputLines = new ArrayList<String>();

    AdbReceiver() {
    }

    @Override
    public void processNewLines(String[] lines) {
        this.adbOutputLines.addAll(Arrays.asList(lines));
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    List<String> getAdbOutputLines() {
        return   this.adbOutputLines;
    }
}
