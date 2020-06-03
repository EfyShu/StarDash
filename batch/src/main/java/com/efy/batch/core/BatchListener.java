package com.efy.batch.core;


import com.efy.batch.dto.ResultData;

public interface BatchListener {
        void singleComplete(ResultData result);
        void piecesComplete(ResultData result);
        void allComplete(ResultData result);
}
