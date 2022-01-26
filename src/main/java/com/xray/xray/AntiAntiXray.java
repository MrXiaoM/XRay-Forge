package com.xray.xray;

import com.xray.etc.RefreshingJob;
import com.xray.etc.Runner;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.ArrayList;
import java.util.List;

public class AntiAntiXray {
    public static List<RefreshingJob> jobs = new ArrayList<>();

    public static void revealNewBlocks(int radX, int radY, int radZ, long delayInMS) {
        RefreshingJob rfj = new RefreshingJob(new Runner(radX, radY, radZ, delayInMS));
        jobs.add(rfj);
    }
}
