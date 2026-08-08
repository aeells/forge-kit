package io.backbone.kit.common.impl.reflect;

record TestNestedRequest(TestRequest request)
{
    public TestRequest getRegisterRequest()
    { return request; }
}
