package io.forge.kit.common.impl.logging;

record TestNestedRequest(TestRequest request)
{
    public TestRequest getRegisterRequest()
    { return request; }
}
