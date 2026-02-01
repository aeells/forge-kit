package io.forge.kit.common.impl.reflect;

record TestNestedRequest(TestRequest request)
{
    public TestRequest getRegisterRequest()
    { return request; }
}
