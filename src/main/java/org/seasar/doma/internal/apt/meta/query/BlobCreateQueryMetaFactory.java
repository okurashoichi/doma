package org.seasar.doma.internal.apt.meta.query;

import static org.seasar.doma.internal.util.AssertionUtil.assertNotNull;

import java.sql.Blob;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import org.seasar.doma.internal.apt.annot.BlobFactoryAnnot;
import org.seasar.doma.internal.apt.meta.dao.DaoMeta;

public class BlobCreateQueryMetaFactory
    extends AbstractCreateQueryMetaFactory<BlobCreateQueryMeta> {

  public BlobCreateQueryMetaFactory(ProcessingEnvironment env) {
    super(env, Blob.class);
  }

  @Override
  public QueryMeta createQueryMeta(ExecutableElement method, DaoMeta daoMeta) {
    assertNotNull(method, daoMeta);
    BlobFactoryAnnot blobFactoryAnnot = BlobFactoryAnnot.newInstance(method, env);
    if (blobFactoryAnnot == null) {
      return null;
    }
    BlobCreateQueryMeta queryMeta = new BlobCreateQueryMeta(method, daoMeta.getDaoElement());
    queryMeta.setBlobFactoryAnnot(blobFactoryAnnot);
    queryMeta.setQueryKind(QueryKind.BLOB_FACTORY);
    doTypeParameters(queryMeta, method, daoMeta);
    doReturnType(queryMeta, method, daoMeta);
    doParameters(queryMeta, method, daoMeta);
    doThrowTypes(queryMeta, method, daoMeta);
    return queryMeta;
  }
}
