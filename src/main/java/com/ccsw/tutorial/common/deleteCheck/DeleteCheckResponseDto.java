package com.ccsw.tutorial.common.deleteCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dgilguti
 *
 * Clase que gestiona el mensaje cuando una entidad se puede o no borrar
 */
public class DeleteCheckResponseDto {
    /**
     *
     * @return True si se puede borrar. False si no
     */
    public boolean isCanDelete() {
        return canDelete;
    }

    /**
     *
     * @param canDelete nuevo valor de {@link #isCanDelete()}
     */
    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    /**
     *
     * @return Razon por la que no se puede borrar
     */
    public String getReason() {
        return reason;
    }

    /**
     *
     * @param reason nuevo  valor de {@link #getReason()}
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     *
     * @return {@link List} de {@link DeleteCheckObject} con la lista de objetos que no permiten borrar esa entidad
     */
    public List<DeleteCheckObject> getList() {
        return list;
    }

    /**
     *
     * @param list nuevo valor de {@link #getList()}
     */
    public void setList(List<DeleteCheckObject> list) {
        this.list = list;
    }
    private boolean canDelete;
    private String reason;
    private List<DeleteCheckObject> list;

    /**
     * Constructor de la clase
     * @param canDelete valor que verifica si es borrable o no
     * @param reason razón por la que se borra
     * @param list {@link List} de {@link DeleteCheckObject} que impiden borrar a la entidad
     */
    public DeleteCheckResponseDto(boolean canDelete, String reason,List<DeleteCheckObject> list) {
        setCanDelete(canDelete);
        setReason(reason);
        setList(list);
    }
}
