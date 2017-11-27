require 'openssl'
require 'open-uri'

class CertificateController < ApplicationController
	def genCsr
		puts "[CertificateController<genCsr>] ==> genCsr"
  	subject = params[:subject]
  	puts "[CertificateController<genCsr>] === subject #{subject}"

  	private_key_filename = 'ee_private_key.pem'
  	key = nil;

		if (File.exist?('cert/'+private_key_filename) == false)
  		#Creating a Key
			key = OpenSSL::PKey::RSA.new 2048
			open 'cert/'+private_key_filename, 'w' do |io| io.write key.to_pem end
		else
			#Loading a Key
			#A key can also be loaded from a file.
			key = OpenSSL::PKey::RSA.new( File.read('cert/'+private_key_filename) )
		end
		puts "[CertificateController<genCsr>] === key #{key}"

		#Certificate Signing Request
		#The CA signs keys through a Certificate Signing Request (CSR). 
		#The CSR contains the information necessary to identify the key.
		name = OpenSSL::X509::Name.parse(subject)
		csr = OpenSSL::X509::Request.new
		csr.version = 2
		csr.subject = name
		csr.public_key = key.public_key
		csr.sign key, OpenSSL::Digest::SHA1.new
		
		#A CSR is saved to disk and sent to the CA for signing.
		# open 'cert/csr.pem', 'w' do |io|
  	# 	io.write csr.to_pem
		# end

		puts "[CertificateController<genCsr>] === csr #{csr}"
    puts "[CertificateController<genCsr>] <== genCsr"
    return send_data csr.to_pem, filename: 'client.csr'
    # return render :json => {
    #   'ReturnCode' => 0, 
    #   'Message' => 'genCsr',
    #   'Result' => csr.to_pem
    # }
  rescue => e
  	puts "[CertificateController] <== ERROR #{e.message}"
    return render json: {'ERROR' => e.message}
	end

  def genCert
  	puts "[CertificateController<genCert>] ==> genCert"
  	csr_content = params[:CsrFile].read()

  	
		puts "[CertificateController<genCert>] === csr_content #{csr_content}"
  	host = "http://192.168.51.97:8086/RAAPI/LoginTest.jsp?sRA_ADDR=http://localhost:8086/iRa/mod/omni/iGnrSvs.run&sAccount=admin&sPass=12345&sLogMsg=Test"
		puts "[CertificateController<genCert>] === host #{host}"
		headers = {};
    response = RestClient.get host, headers
		res = response.to_s
		puts res
		session_id = 0;
		res.each_line {|s| 
			if(s.include?("SessionId"))
				#puts "[CertificateController<genCert>] === SessionId #{s}"
				arr = s.split('=')
				session_id = arr[1].to_i;
			end
		}
		puts "[CertificateController<genCert>] === session_id #{session_id}"
  
  	timestamp = Time.now.to_i

		host = "http://192.168.51.97:8086/RAAPI/ApplyCert.jsp?sRA_ADDR=http%3A%2F%2Flocalhost%3A8086%2FiRa%2Fmod%2Fomni%2FiGnrSvs.run&sAccount=admin&sPass=12345&sLogMsg=Test&exportFlag=0&protectFlag=1&keyLength=1024&sSessionID=222&sCertType=ONE_FULL&sEmail=@@&sCN=P267255524&sCertReq=#{CGI::escape(csr_content)}&sUserType=0&sQueue=false"
		puts "[CertificateController<genCert>] === host #{host}"
		headers = {};
    response = RestClient.get host, headers
		res = response.to_s

		apply_id = 200936;
		res.each_line {|s| 
			if(s.include?("ApplyID"))
				puts "[CertificateController<genCert>] === ApplyCert #{s}"
				arr = s.split('=,<')
				apply_id = arr[1].to_i;
				break;
			end
		}
		puts "[CertificateController<genCert>] === apply_id #{apply_id}"


		host = "http://192.168.51.97:8086/RAAPI/SyncCert.jsp?sRA_ADDR=http://localhost:8086/iRa/mod/omni/iGnrSvs.run&sAccount=admin&sPass=12345&sLogMsg=Test&sSessionID&sApplyID=#{apply_id}"
		puts "[CertificateController<genCert>] === host #{host}"
		headers = {};
    response = RestClient.get host, headers
		res = response.to_s
		status = 999;
		res.each_line {|s| 
			if(s.include?("SyncCert"))
				puts "[CertificateController<genCert>] === ApplyCert #{s}"
				arr = s.split('=,<')
				status = arr[1].to_i;
				break;
			end
		}
		puts "[CertificateController<genCert>] === status #{status}"

		host = "http://192.168.51.97:8086/RAAPI/FetchCert.jsp?sRA_ADDR=http://localhost:8086/iRa/mod/omni/iGnrSvs.run&sCertFormat=X509&sLogMsg=Test&sApplyID=#{apply_id}"
		puts "[CertificateController<genCert>] === host #{host}"
		headers = {};
    response = RestClient.get host, headers
		res = response.to_s
		cert_content = "";
		res.each_line {|s| 
			if(s.include?("name='certValue'"))
				#puts "[CertificateController<genCert>] === ApplyCert #{s}"
				arr = s.split('>')
				puts arr[1] 
				cert_content = arr[1];
			elsif(cert_content != "" && s.include?("</textarea>") == false)
				cert_content += s;
			end
			if(s.include?("</textarea>"))
				break;
			end
		}
		puts "[CertificateController<genCert>] === cert_content #{cert_content}"

  # 	private_key_filename = 'root_ca_key.pem'
  # 	ca_key = nil;

		# if (File.exist?('cert/'+private_key_filename) == false)
  # 		#Creating a Key
		# 	ca_key = OpenSSL::PKey::RSA.new 2048
		# 	open 'cert/'+private_key_filename, 'w' do |io| io.write ca_key.to_pem end
		# else
		# 	#Loading a Key
		# 	#A key can also be loaded from a file.
		# 	ca_key = OpenSSL::PKey::RSA.new( File.read('cert/' + private_key_filename) )
		# end
		# puts "[CertificateController<genCert>] === ca_key #{ca_key}"

		# ca_cert = OpenSSL::X509::Certificate.new File.read 'cert/root_ca.crt'
		# puts "[CertificateController<genCert>] === ca_cert #{ca_cert}"

  #   #Creating a Certificate
		# csr = OpenSSL::X509::Request.new(csr_content)
		# #csr = OpenSSL::X509::Request.new File.read 'cert/csr.pem'
		# raise 'CSR can not be verified' unless csr.verify csr.public_key
		
		# #After verification a certificate is created, marked for various usages, 
		# #signed with the CA key and returned to the requester.
		# csr_cert = OpenSSL::X509::Certificate.new
		# csr_cert.serial = 0
		# csr_cert.version = 2
		# csr_cert.not_before = Time.now
		# csr_cert.not_after = Time.now + 30.days

		# csr_cert.subject = csr.subject
		# csr_cert.public_key = csr.public_key
		# csr_cert.issuer = ca_cert.subject
		
		# extension_factory = OpenSSL::X509::ExtensionFactory.new
		# extension_factory.subject_certificate = csr_cert
		# extension_factory.issuer_certificate = ca_cert

		# csr_cert.add_extension    extension_factory.create_extension('basicConstraints', 'CA:FALSE')
		# csr_cert.add_extension    extension_factory.create_extension('keyUsage', 'keyEncipherment,dataEncipherment,digitalSignature')
		# csr_cert.add_extension    extension_factory.create_extension('subjectKeyIdentifier', 'hash')

		# csr_cert.sign ca_key, OpenSSL::Digest::SHA1.new

		# open 'cert/client.crt', 'w' do |io|
		#   io.write csr_cert.to_pem
		# end

		# puts "[CertificateController<genCert>] === csr_cert #{csr_cert}"
    puts "[CertificateController<genCert>] <== genCert"
    return send_data cert_content, filename: 'client.crt'
    # return render :json => {
    #   'ReturnCode' => 0, 
    #   'Message' => 'genCert',
    #   'Result' => cert_content
    # }
  rescue => e
  	puts "[CertificateController] <== ERROR #{e.message}"
    return render json: {'ERROR' => e.message}
  end
  
  def signature
  	puts "[CertificateController<signature>] ==>"
		orignal_message = params[:orignal_message]
  	puts "[CertificateController<signature>] === orignal_message #{orignal_message}"

		p12_file = params[:p12]
		password = params[:password]
		p12 = OpenSSL::PKCS12.new(p12_file.read(), password)
	
		ee_key = p12.key
		ee_cert = p12.certificate
  	
		ca_cert = nil
		if (File.exist?('cert/root_ca.crt') == false)
			raise "Do not find ca certificate"
		else
			ca_cert = OpenSSL::X509::Certificate.new File.read 'cert/root_ca.crt'
		end

		p7b = OpenSSL::PKCS7::sign(ee_cert, ee_key, orignal_message)
		#puts "[CertificateController<signature>] == p7b #{p7b}"

		cert_store = OpenSSL::X509::Store.new()
		#cert_store.add_cert(ee_cert)
		#cert_store.add_cert(ca_cert)

		if p7b.verify([ee_cert], cert_store)
			puts "[CertificateController<signature>] verify OK #{p7b.data}"
		else
			puts "[CertificateController<signature>] verify fail"
		end

  	puts "[CertificateController<signature>] <=="
  	return send_data p7b.to_pem, filename: 'message.p7b'
  rescue => e
    return render json: {'ERROR' => e.message}
  end

  def verify
  	puts "[CertificateController<verify>] ==> #{params}"

  	original_filename = params[:P7bFile].original_filename
    temp_file = params[:P7bFile].read()

    p7b = OpenSSL::PKCS7.new(temp_file)
    #cert_array = p7b.certificates()
		
		client_cert = nil
		if (File.exist?('cert/ee.crt') == false)
			raise "Do not find user certificate"
		else
			client_cert = OpenSSL::X509::Certificate.new File.read 'cert/ee.crt'
		end
		#puts "[CertificateController<signature>] === client_cert #{client_cert}"
		
		ca_cert = nil
		if (File.exist?('cert/root_ca.crt') == false)
			raise "Do not find ca certificate"
		else
			ca_cert = OpenSSL::X509::Certificate.new File.read 'cert/root_ca.crt'
		end

		#cert_store = OpenSSL::X509::Store.new(cert_array)
		cert_store = OpenSSL::X509::Store.new
		cert_store.add_cert(client_cert)
		cert_store.add_cert(ca_cert)

		if p7b.verify([client_cert], cert_store)
			puts "[CertificateController<verify>] verify OK #{p7b.data}"
		else
			puts "[CertificateController<verify>] verify fail #{p7b.data}"
		end


  	puts "[CertificateController<verify>] <=="
  	return render :json => {
      'ReturnCode' => 0, 
      'Message' => 'va',
      'Result' => p7b.data
    }
  rescue => e
    return render json: {'ERROR' => e.message}
  end

  def autoGenPfx
  	puts "[CertificateController<autoGenCert>] ==>"
  	subject = params[:subject]
  	password = params[:password]
  	puts "[CertificateController<autoGenCert>] === subject #{subject}"
		timestamp = Time.now.to_i
  	key = OpenSSL::PKey::RSA.new(2048) ;
  	
  	# private_key_filename = "ee_private_key#{timestamp}.pem"
		# open 'cert/'+private_key_filename, 'w' do |io| io.write key.to_pem end

		#Certificate Signing Request
		#The CA signs keys through a Certificate Signing Request (CSR). 
		#The CSR contains the information necessary to identify the key.
		name = OpenSSL::X509::Name.parse(subject)
		csr = OpenSSL::X509::Request.new
		csr.version = 2
		csr.subject = name
		csr.public_key = key.public_key
		csr.sign key, OpenSSL::Digest::SHA1.new
  	csr_content = csr.to_pem();
		puts "[CertificateController<autoGenCert>] === csr_content #{csr_content}"

		## LoginTest.jsp
  	host = "http://192.168.51.97:8086/RAAPI/LoginTest.jsp?sRA_ADDR=http://localhost:8086/iRa/mod/omni/iGnrSvs.run&sAccount=admin&sPass=12345&sLogMsg=Test"
		puts "[CertificateController<autoGenCert>] === host #{host}"
		headers = {};
    response = RestClient.get host, headers
		res = response.to_s
		session_id = 0;
		res.each_line {|s| 
			if(s.include?("SessionId"))
				#puts "[CertificateController<genCert>] === SessionId #{s}"
				arr = s.split('=')
				session_id = arr[1].to_i;
			end
		}
		puts "[CertificateController<genCert>] === session_id #{session_id}"
  
	  ## ApplyCert.jsp
  	test_cn = "Bill.Rails.#{timestamp}" 
		host = "http://192.168.51.97:8086/RAAPI/ApplyCert.jsp?sRA_ADDR=http%3A%2F%2Flocalhost%3A8086%2FiRa%2Fmod%2Fomni%2FiGnrSvs.run&sAccount=admin&sPass=12345&sLogMsg=Test&exportFlag=0&protectFlag=1&keyLength=1024&sSessionID=222&sCertType=ONE_FULL&sEmail=@@&sCN=#{test_cn}&sCertReq=#{CGI::escape(csr_content)}&sUserType=0&sQueue=false"
		puts "[CertificateController<genCert>] === host #{host}"
		headers = {};
    response = RestClient.get host, headers
		res = response.to_s

		apply_id = 0;
		res.each_line {|s| 
			if(s.include?("ApplyID"))
				puts "[CertificateController<genCert>] === ApplyCert #{s}"
				arr = s.split('=')
				arr = arr[1].split('=')
				apply_id = arr[0].to_i;
				break;
			end
		}
		puts "[CertificateController<genCert>] === apply_id #{apply_id}"
		if(apply_id == 0)
			raise "Can not get Apply ID"
		end

		## SyncCert.jsp
		host = "http://192.168.51.97:8086/RAAPI/SyncCert.jsp?sRA_ADDR=http://localhost:8086/iRa/mod/omni/iGnrSvs.run&sAccount=admin&sPass=12345&sLogMsg=Test&sSessionID&sApplyID=#{apply_id}"
		puts "[CertificateController<genCert>] === host #{host}"
		headers = {};
    response = RestClient.get host, headers
		res = response.to_s
		status = 999;
		res.each_line {|s| 
			if(s.include?("SyncCert"))
				puts "[CertificateController<genCert>] === ApplyCert #{s}"
				arr = s.split('=,<')
				status = arr[1].to_i;
				break;
			end
		}
		puts "[CertificateController<genCert>] === status #{status}"

		## FetchCert.jsp
		host = "http://192.168.51.97:8086/RAAPI/FetchCert.jsp?sRA_ADDR=http://localhost:8086/iRa/mod/omni/iGnrSvs.run&sCertFormat=X509&sLogMsg=Test&sApplyID=#{apply_id}"
		puts "[CertificateController<genCert>] === host #{host}"
		headers = {};
    response = RestClient.get host, headers
		res = response.to_s
		cert_content = "";
		res.each_line {|s| 
			if(s.include?("name='certValue'"))
				#puts "[CertificateController<genCert>] === ApplyCert #{s}"
				arr = s.split('>')
				puts arr[1] 
				cert_content = arr[1];
			elsif(cert_content != "" && s.include?("</textarea>") == false)
				cert_content += s;
			end
			if(s.include?("</textarea>"))
				break;
			end
		}
		puts "[CertificateController<autoGenCert>] === cert_content #{cert_content}"

		## convert to p12
		client_cert = OpenSSL::X509::Certificate.new(cert_content);
		p12 = OpenSSL::PKCS12.create(password, test_cn, key, client_cert)
    puts "[CertificateController<autoGenCert>] <== autoGenCert"
    return send_data p12.to_der, filename: 'client.p12'
  rescue => e
  	puts "[CertificateController] <== ERROR #{e.message}"
    return render json: {'ERROR' => e.message}
  end
end
