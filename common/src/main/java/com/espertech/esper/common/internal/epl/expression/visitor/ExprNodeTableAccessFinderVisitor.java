/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package com.espertech.esper.common.internal.epl.expression.visitor;

import com.espertech.esper.common.internal.epl.expression.core.ExprNode;
import com.espertech.esper.common.internal.epl.expression.subquery.ExprSubselectNode;
import com.espertech.esper.common.internal.epl.expression.table.ExprTableAccessNode;

public class ExprNodeTableAccessFinderVisitor implements ExprNodeVisitor {
    private boolean hasTableAccess;

    public ExprNodeTableAccessFinderVisitor() {
    }

    public boolean isVisit(ExprNode exprNode) {
        return !hasTableAccess;
    }

    public void visit(ExprNode exprNode) {
        if (exprNode instanceof ExprTableAccessNode) {
            hasTableAccess = true;
        }
        if (exprNode instanceof ExprSubselectNode) {
            ExprSubselectNode subselect = (ExprSubselectNode) exprNode;
            if (subselect.getRawEventType() != null) {
                hasTableAccess |= subselect.getRawEventType().getMetadata().getTypeClass().isTable();
            }
        }
    }

    public boolean isHasTableAccess() {
        return hasTableAccess;
    }
}
