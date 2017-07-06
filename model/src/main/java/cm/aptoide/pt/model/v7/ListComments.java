/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.model.v7;

import cm.aptoide.pt.model.v7.base.BaseV7EndlessDataListResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created on 20/07/16.
 */
@EqualsAndHashCode(callSuper = true) @Data public class ListComments
    extends BaseV7EndlessDataListResponse<Comment> {
}
