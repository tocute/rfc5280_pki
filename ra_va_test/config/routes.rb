Rails.application.routes.draw do
  post 'certificate/gencsr' => 'certificate#genCsr'
  post 'certificate/auto_get_pfx' => 'certificate#autoGenPfx'
  post 'certificate/ra' => 'certificate#genCert'
  post 'certificate/ca' => 'certificate#signature'
  post 'certificate/va' => 'certificate#verify'

  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html
end
