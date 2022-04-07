package org.koprivnjak.zavrsni.ai;


import net.andreinc.neatchess.client.model.Move;

public interface OnMoveFoundListener {
    void onMoveFound(Move move);
}
