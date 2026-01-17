package io.forge.kit.common.logging;

record TestNestedRequest(TestRequest request)
{
    public TestRequest getRegisterRequest()
    { return request; }
}
