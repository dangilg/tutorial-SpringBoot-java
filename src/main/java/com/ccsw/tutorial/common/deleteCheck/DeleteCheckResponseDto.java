package com.ccsw.tutorial.common.deleteCheck;

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

    private boolean canDelete;
    private String reason;

    public DeleteCheckResponseDto(boolean canDelete, String reason) {
        setCanDelete(canDelete);
        setReason(reason);

    }
}
