require 'test_helper'

class CertificateControllerTest < ActionDispatch::IntegrationTest
  test "should get ca" do
    get certificate_ca_url
    assert_response :success
  end

end
