package com.ccsw.tutorial.common.deleteCheck;

import java.util.ArrayList;
import java.util.List;

public class DeleteCheckResponseDto {
    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<DeleteCheckObject> getList() {
        return list;
    }

    public void setList(List<DeleteCheckObject> list) {
        this.list = list;
    }
    private boolean canDelete;
    private String reason;
    private List<DeleteCheckObject> list;

    public DeleteCheckResponseDto(boolean canDelete, String reason,List<DeleteCheckObject> list) {
        setCanDelete(canDelete);
        setReason(reason);
        setList(list);
    }
}
